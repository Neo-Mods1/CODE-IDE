package com.termux.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.system.Os;
import android.util.Pair;
import android.util.Log;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class TermuxInstaller {

    private static final String LOG_TAG = "TermuxInstaller";

    private static File sPrefixDir;
    private static File sStagingDir;

    private static synchronized void initPaths(Context context) {
        if (sPrefixDir == null) {
            File filesDir = context.getFilesDir();
            sPrefixDir = new File(filesDir, "usr");
            sStagingDir = new File(filesDir, "usr-staging");
        }
    }

    public static synchronized File getPrefixDir(Context context) {
        initPaths(context);
        return sPrefixDir;
    }

    public static String getPrefixPath(Context context) {
        return getPrefixDir(context).getAbsolutePath();
    }

    public static boolean isBootstrapInstalled(Context context) {
        File prefix = getPrefixDir(context);
        File bash = new File(prefix, "bin/bash");
        return prefix.exists() && bash.exists() && bash.canExecute() && prefix.list() != null && prefix.list().length > 0;
    }

    public interface SetupCallback {
        void onSuccess();
        void onError(String message);
    }

    public static void setupBootstrapIfNeeded(final Activity activity, final SetupCallback callback) {
        initPaths(activity);

        if (isBootstrapInstalled(activity)) {
            callback.onSuccess();
            return;
        }

        final Dialog[] progressDialog = new Dialog[1];
        activity.runOnUiThread(() -> {
            try {
                progressDialog[0] = new AlertDialog.Builder(activity)
                    .setTitle("Installing")
                    .setMessage("Installing bootstrap packages...")
                    .setCancelable(false)
                    .create();
                progressDialog[0].show();
            } catch (WindowManager.BadTokenException e) {
                // Activity dismissed
            }
        });

        new Thread(() -> {
            try {
                File filesDir = activity.getFilesDir();
                File stagingDir = sStagingDir;
                File prefixDir = sPrefixDir;

                // Clean up only staging from previous failed attempts
                deleteRecursive(stagingDir);

                // Ensure parent (filesDir) exists
                if (!filesDir.exists() && !filesDir.mkdirs()) {
                    throw new RuntimeException("Failed to create files directory: " + filesDir.getAbsolutePath());
                }

                // Create staging directory
                if (!stagingDir.exists() && !stagingDir.mkdirs()) {
                    throw new RuntimeException("Failed to create staging directory: " + stagingDir.getAbsolutePath());
                }

                final byte[] buffer = new byte[8192];
                final List<Pair<String, String>> symlinks = new ArrayList<>(50);

                final byte[] zipBytes = loadZipBytes();
                Log.i(LOG_TAG, "Bootstrap ZIP size: " + zipBytes.length + " bytes");

                try (ZipInputStream zipInput = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
                    ZipEntry zipEntry;
                    while ((zipEntry = zipInput.getNextEntry()) != null) {
                        if (zipEntry.getName().equals("SYMLINKS.txt")) {
                            BufferedReader symlinksReader = new BufferedReader(new InputStreamReader(zipInput));
                            String line;
                            while ((line = symlinksReader.readLine()) != null) {
                                String[] parts = line.split("\u2190"); // ← character
                                if (parts.length != 2) {
                                    Log.w(LOG_TAG, "Skipping malformed symlink line: " + line);
                                    continue;
                                }
                                String oldPath = parts[0];
                                String newPath = stagingDir.getAbsolutePath() + "/" + parts[1];
                                symlinks.add(Pair.create(oldPath, newPath));

                                File parent = new File(newPath).getParentFile();
                                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                                    throw new RuntimeException("Failed to create directory: " + parent.getAbsolutePath());
                                }
                            }
                        } else {
                            // ZIP entries do NOT have usr/ prefix
                            // They are flat: bin/bash, lib/libfoo.so, etc.
                            String zipEntryName = zipEntry.getName();
                            File targetFile = new File(stagingDir, zipEntryName);
                            boolean isDirectory = zipEntry.isDirectory();

                            File parentDir = isDirectory ? targetFile : targetFile.getParentFile();
                            if (parentDir != null && !parentDir.exists()) {
                                if (!parentDir.mkdirs() && !parentDir.exists()) {
                                    throw new RuntimeException("Failed to create directory: " + parentDir.getAbsolutePath());
                                }
                            }

                            if (!isDirectory) {
                                try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
                                    int readBytes;
                                    while ((readBytes = zipInput.read(buffer)) != -1)
                                        outStream.write(buffer, 0, readBytes);
                                }
                                // Set executable permissions on binaries — match AndroidIDE exactly
                                if (zipEntryName.startsWith("bin/") || zipEntryName.startsWith("libexec") ||
                                    zipEntryName.startsWith("lib/apt/apt-helper") || zipEntryName.startsWith("lib/apt/methods")) {
                                    //noinspection OctalInteger
                                    Os.chmod(targetFile.getAbsolutePath(), 0700);
                                }
                                // Also use File API as fallback for all files
                                targetFile.setReadable(true, false);
                                targetFile.setExecutable(true, false);
                            }
                        }
                    }
                }

                if (symlinks.isEmpty()) {
                    throw new RuntimeException("No SYMLINKS.txt entries encountered in bootstrap zip");
                }

                // Create symlinks
                for (Pair<String, String> symlink : symlinks) {
                    try {
                        Os.symlink(symlink.first, symlink.second);
                    } catch (Exception e) {
                        Log.w(LOG_TAG, "Failed to create symlink: " + symlink.second + " -> " + symlink.first + ": " + e.getMessage());
                    }
                }

                // Ensure bin/ executables are definitely executable
                ensureExecutable(new File(stagingDir, "bin"));
                ensureExecutable(new File(stagingDir, "libexec"));

                // Move staging to final — delete old prefix first
                deleteRecursive(prefixDir);
                if (!stagingDir.renameTo(prefixDir)) {
                    throw new RuntimeException("Failed to move staging to prefix directory");
                }

                // Final verification: ensure bash is executable in the final location
                File bashInPrefix = new File(prefixDir, "bin/bash");
                if (!bashInPrefix.canExecute()) {
                    Log.w(LOG_TAG, "bash not executable after install, retrying chmod");
                    //noinspection OctalInteger
                    Os.chmod(bashInPrefix.getAbsolutePath(), 0700);
                    bashInPrefix.setExecutable(true, false);
                    if (!bashInPrefix.canExecute()) {
                        throw new RuntimeException("bash is not executable after chmod: " + bashInPrefix.getAbsolutePath());
                    }
                }

                Log.i(LOG_TAG, "Bootstrap installed successfully to " + prefixDir.getAbsolutePath());
                activity.runOnUiThread(() -> {
                    try { progressDialog[0].dismiss(); } catch (Exception ignored) {}
                    callback.onSuccess();
                });

            } catch (final Exception e) {
                String msg = e.getMessage() != null ? e.getMessage() : e.toString();
                Log.e(LOG_TAG, "Bootstrap setup failed: " + msg, e);
                activity.runOnUiThread(() -> {
                    try { progressDialog[0].dismiss(); } catch (Exception ignored) {}
                    callback.onError(msg);
                });
            }
        }).start();
    }

    private static void ensureExecutable(File dir) {
        if (!dir.exists() || !dir.isDirectory()) return;
        File[] children = dir.listFiles();
        if (children == null) return;
        for (File child : children) {
            if (child.isFile() && !child.getName().contains(".")) {
                // Binary without extension — ensure executable
                try {
                    //noinspection OctalInteger
                    Os.chmod(child.getAbsolutePath(), 0700);
                } catch (Exception e) {
                    child.setExecutable(true, false);
                }
            }
        }
    }

    private static void deleteRecursive(File file) {
        if (file == null || !file.exists()) return;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        file.delete();
    }

    static void setupStorageSymlinks(final Context context) {
        new Thread(() -> {
            try {
                File storageDir = new File(getPrefixDir(context), "home/storage");
                if (storageDir.exists()) deleteRecursive(storageDir);
                storageDir.mkdirs();

                Os.symlink(Environment.getExternalStorageDirectory().getAbsolutePath(),
                    new File(storageDir, "shared").getAbsolutePath());
                Os.symlink(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),
                    new File(storageDir, "downloads").getAbsolutePath());
                Os.symlink(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath(),
                    new File(storageDir, "dcim").getAbsolutePath());
                Os.symlink(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(),
                    new File(storageDir, "pictures").getAbsolutePath());
                Os.symlink(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath(),
                    new File(storageDir, "music").getAbsolutePath());
                Os.symlink(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath(),
                    new File(storageDir, "movies").getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static byte[] loadZipBytes() {
        System.loadLibrary("termux-bootstrap");
        return getZip();
    }

    public static native byte[] getZip();
}

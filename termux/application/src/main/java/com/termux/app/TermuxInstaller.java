package com.termux.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
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
        return prefix.exists() && bash.exists() && prefix.list() != null && prefix.list().length > 0;
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
                        String entryName = zipEntry.getName();

                        if (entryName.equals("SYMLINKS.txt")) {
                            BufferedReader symlinksReader = new BufferedReader(new InputStreamReader(zipInput));
                            String line;
                            while ((line = symlinksReader.readLine()) != null) {
                                String[] parts = line.split("\u2190"); // ← character
                                if (parts.length != 2) {
                                    Log.w(LOG_TAG, "Skipping malformed symlink line: " + line);
                                    continue;
                                }
                                String symlinkTarget = parts[0];
                                String symlinkLink = parts[1];

                                // Rewrite symlink target: replace com.termux paths with our actual prefix path
                                symlinkTarget = symlinkTarget
                                    .replace("/data/data/com.termux/files/usr", prefixDir.getAbsolutePath())
                                    .replace("/data/user/0/com.termux/files/usr", prefixDir.getAbsolutePath());

                                // The link path from SYMLINKS.txt is relative like "bin/bash"
                                // We need to resolve it relative to our staging dir
                                File linkFile;
                                if (symlinkLink.startsWith("usr/")) {
                                    // Strip usr/ prefix since we extract directly into staging
                                    linkFile = new File(stagingDir, symlinkLink.substring(4));
                                } else {
                                    linkFile = new File(stagingDir, symlinkLink);
                                }

                                symlinks.add(Pair.create(symlinkTarget, linkFile.getAbsolutePath()));

                                File parent = linkFile.getParentFile();
                                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                                    throw new RuntimeException("Failed to create directory: " + parent.getAbsolutePath());
                                }
                            }
                        } else {
                            // Strip "usr/" prefix from ZIP entries since we extract directly into staging
                            // ZIP entries look like: "usr/bin/bash", "usr/lib/libfoo.so", etc.
                            if (entryName.startsWith("usr/")) {
                                entryName = entryName.substring(4);
                            }
                            // Skip empty entries (just "usr/" directory itself)
                            if (entryName.isEmpty()) continue;

                            File targetFile = new File(stagingDir, entryName);
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

                // Use system chmod to set permissions — more reliable than Os.chmod on Android
                String[] chmodTargets = {"bin", "lib", "libexec", "share"};
                for (String target : chmodTargets) {
                    File dir = new File(stagingDir, target);
                    if (dir.exists()) {
                        Process p = Runtime.getRuntime().exec(
                            new String[]{"/system/bin/chmod", "-R", "755", dir.getAbsolutePath()});
                        p.waitFor();
                    }
                }

                // Move staging to final — delete old prefix first
                deleteRecursive(prefixDir);
                if (!stagingDir.renameTo(prefixDir)) {
                    // Rename failed (cross-device?), fall back to copy
                    copyRecursive(stagingDir, prefixDir);
                    deleteRecursive(stagingDir);
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

    private static void chmodRecursive(File dir, int mode) {
        if (!dir.exists()) return;
        if (dir.isDirectory()) {
            //noinspection OctalInteger
            dir.setExecutable(true, true);
            dir.setReadable(true, true);
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    chmodRecursive(child, mode);
                }
            }
        } else {
            try {
                //noinspection OctalInteger
                Os.chmod(dir.getAbsolutePath(), mode);
            } catch (Exception e) {
                // Ignore chmod errors on individual files
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

    private static void copyRecursive(File src, File dest) throws Exception {
        if (src.isDirectory()) {
            dest.mkdirs();
            File[] children = src.listFiles();
            if (children != null) {
                for (File child : children) {
                    copyRecursive(child, new File(dest, child.getName()));
                }
            }
        } else {
            java.io.FileInputStream in = new java.io.FileInputStream(src);
            java.io.FileOutputStream out = new java.io.FileOutputStream(dest);
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            in.close();
            out.close();
        }
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

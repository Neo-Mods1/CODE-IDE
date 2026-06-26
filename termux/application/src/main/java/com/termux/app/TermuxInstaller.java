package com.termux.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.system.Os;
import android.util.Pair;
import android.view.WindowManager;

import com.termux.shared.termux.TermuxConstants;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.termux.shared.termux.TermuxConstants.TERMUX_PREFIX_DIR;
import static com.termux.shared.termux.TermuxConstants.TERMUX_PREFIX_DIR_PATH;
import static com.termux.shared.termux.TermuxConstants.TERMUX_STAGING_PREFIX_DIR;
import static com.termux.shared.termux.TermuxConstants.TERMUX_STAGING_PREFIX_DIR_PATH;

public final class TermuxInstaller {

    private static final String LOG_TAG = "TermuxInstaller";

    public interface SetupCallback {
        void onSuccess();
        void onError(String message);
    }

    public static boolean isBootstrapInstalled() {
        File prefixDir = new File(TERMUX_PREFIX_DIR_PATH);
        File bash = new File(prefixDir, "bin/bash");
        return prefixDir.exists() && prefixDir.list() != null && prefixDir.list().length > 0 && bash.exists();
    }

    public static void setupBootstrapIfNeeded(final Activity activity, final SetupCallback callback) {
        if (isBootstrapInstalled()) {
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
                // Clean up previous attempts
                deleteRecursive(new File(TERMUX_STAGING_PREFIX_DIR_PATH));
                deleteRecursive(new File(TERMUX_PREFIX_DIR_PATH));

                // Create staging directory
                File stagingDir = new File(TERMUX_STAGING_PREFIX_DIR_PATH);
                if (!stagingDir.mkdirs() && !stagingDir.exists()) {
                    throw new RuntimeException("Failed to create staging directory: " + TERMUX_STAGING_PREFIX_DIR_PATH);
                }

                final byte[] buffer = new byte[8192];
                final List<Pair<String, String>> symlinks = new ArrayList<>(50);

                final byte[] zipBytes = loadZipBytes();
                try (ZipInputStream zipInput = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
                    ZipEntry zipEntry;
                    while ((zipEntry = zipInput.getNextEntry()) != null) {
                        if (zipEntry.getName().equals("SYMLINKS.txt")) {
                            BufferedReader symlinksReader = new BufferedReader(new InputStreamReader(zipInput));
                            String line;
                            while ((line = symlinksReader.readLine()) != null) {
                                String[] parts = line.split("←");
                                if (parts.length != 2)
                                    throw new RuntimeException("Malformed symlink line: " + line);
                                // Rewrite symlink target: replace com.termux paths with our actual prefix path
                                String oldPath = parts[0]
                                    .replace("/data/data/com.termux/files/usr", TERMUX_PREFIX_DIR_PATH)
                                    .replace("/data/user/0/com.termux/files/usr", TERMUX_PREFIX_DIR_PATH);
                                String newPath = TERMUX_STAGING_PREFIX_DIR_PATH + "/" + parts[1];
                                symlinks.add(Pair.create(oldPath, newPath));

                                File parent = new File(newPath).getParentFile();
                                if (parent != null && !parent.exists()) {
                                    if (!parent.mkdirs() && !parent.exists()) {
                                        throw new RuntimeException("Failed to create directory: " + parent.getAbsolutePath());
                                    }
                                }
                            }
                        } else {
                            String zipEntryName = zipEntry.getName();
                            File targetFile = new File(TERMUX_STAGING_PREFIX_DIR_PATH, zipEntryName);
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
                                //noinspection OctalInteger
                                Os.chmod(targetFile.getAbsolutePath(), 0700);
                            }
                        }
                    }
                }

                if (symlinks.isEmpty())
                    throw new RuntimeException("No SYMLINKS.txt encountered in bootstrap zip");
                for (Pair<String, String> symlink : symlinks) {
                    Os.symlink(symlink.first, symlink.second);
                }

                // Ensure all bin/ and lib/ executables have correct permissions
                chmodRecursive(new File(TERMUX_STAGING_PREFIX_DIR_PATH, "bin"), 0700);
                chmodRecursive(new File(TERMUX_STAGING_PREFIX_DIR_PATH, "lib"), 0700);
                chmodRecursive(new File(TERMUX_STAGING_PREFIX_DIR_PATH, "libexec"), 0700);

                // Move staging to final
                if (!TERMUX_STAGING_PREFIX_DIR.renameTo(TERMUX_PREFIX_DIR)) {
                    copyRecursive(new File(TERMUX_STAGING_PREFIX_DIR_PATH), new File(TERMUX_PREFIX_DIR_PATH));
                    deleteRecursive(new File(TERMUX_STAGING_PREFIX_DIR_PATH));
                }

                activity.runOnUiThread(() -> {
                    try { progressDialog[0].dismiss(); } catch (Exception ignored) {}
                    callback.onSuccess();
                });

            } catch (final Exception e) {
                String msg = e.getMessage() != null ? e.getMessage() : e.toString();
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
                File storageDir = new File(TermuxConstants.TERMUX_HOME_DIR_PATH, "storage");
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

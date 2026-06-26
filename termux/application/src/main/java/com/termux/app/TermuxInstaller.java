package com.termux.app;

import android.app.Activity;
import android.app.AlertDialog;
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
        // If prefix already exists and has content, skip
        if (isBootstrapInstalled()) {
            callback.onSuccess();
            return;
        }

        activity.runOnUiThread(() -> {
            try {
                new AlertDialog.Builder(activity)
                    .setTitle("Installing")
                    .setMessage("Installing bootstrap packages...")
                    .setCancelable(false)
                    .show();
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
                stagingDir.mkdirs();

                // Create final prefix directory
                File prefixDir = new File(TERMUX_PREFIX_DIR_PATH);
                prefixDir.mkdirs();

                // Load and extract bootstrap zip
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
                                String oldPath = parts[0];
                                String newPath = TERMUX_STAGING_PREFIX_DIR_PATH + "/" + parts[1];
                                symlinks.add(Pair.create(oldPath, newPath));

                                File parent = new File(newPath).getParentFile();
                                if (parent != null && !parent.exists()) parent.mkdirs();
                            }
                        } else {
                            String zipEntryName = zipEntry.getName();
                            File targetFile = new File(TERMUX_STAGING_PREFIX_DIR_PATH, zipEntryName);
                            boolean isDirectory = zipEntry.isDirectory();

                            File parentDir = isDirectory ? targetFile : targetFile.getParentFile();
                            if (parentDir != null && !parentDir.exists()) parentDir.mkdirs();

                            if (!isDirectory) {
                                try (FileOutputStream outStream = new FileOutputStream(targetFile)) {
                                    int readBytes;
                                    while ((readBytes = zipInput.read(buffer)) != -1)
                                        outStream.write(buffer, 0, readBytes);
                                }
                                if (zipEntryName.startsWith("bin/") || zipEntryName.startsWith("libexec") ||
                                    zipEntryName.startsWith("lib/apt/apt-helper") || zipEntryName.startsWith("lib/apt/methods")) {
                                    //noinspection OctalInteger
                                    Os.chmod(targetFile.getAbsolutePath(), 0700);
                                }
                            }
                        }
                    }
                }

                if (symlinks.isEmpty())
                    throw new RuntimeException("No SYMLINKS.txt encountered in bootstrap zip");
                for (Pair<String, String> symlink : symlinks) {
                    Os.symlink(symlink.first, symlink.second);
                }

                // Move staging to final
                if (!TERMUX_STAGING_PREFIX_DIR.renameTo(TERMUX_PREFIX_DIR)) {
                    // Fallback: copy files if rename fails (different filesystem)
                    copyRecursive(new File(TERMUX_STAGING_PREFIX_DIR_PATH), new File(TERMUX_PREFIX_DIR_PATH));
                    deleteRecursive(new File(TERMUX_STAGING_PREFIX_DIR_PATH));
                }

                activity.runOnUiThread(() -> callback.onSuccess());

            } catch (final Exception e) {
                String msg = e.getMessage() != null ? e.getMessage() : e.toString();
                activity.runOnUiThread(() -> callback.onError(msg));
            }
        }).start();
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

                File sharedDir = Environment.getExternalStorageDirectory();
                Os.symlink(sharedDir.getAbsolutePath(), new File(storageDir, "shared").getAbsolutePath());

                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                Os.symlink(downloadsDir.getAbsolutePath(), new File(storageDir, "downloads").getAbsolutePath());

                File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                Os.symlink(dcimDir.getAbsolutePath(), new File(storageDir, "dcim").getAbsolutePath());

                File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                Os.symlink(picturesDir.getAbsolutePath(), new File(storageDir, "pictures").getAbsolutePath());

                File musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                Os.symlink(musicDir.getAbsolutePath(), new File(storageDir, "music").getAbsolutePath());

                File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                Os.symlink(moviesDir.getAbsolutePath(), new File(storageDir, "movies").getAbsolutePath());
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

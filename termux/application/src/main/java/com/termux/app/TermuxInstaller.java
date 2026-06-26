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

/**
 * Install bootstrap packages. Follows AndroidIDE's TermuxInstaller exactly:
 * (1) If $PREFIX already exists and bash is executable, done.
 * (2) Show progress dialog.
 * (3) Clear staging directory.
 * (4) Load ZIP from native lib.
 * (5) Extract ZIP entries to staging, chmod bin/libexec/lib/apt.
 * (6) Create symlinks from SYMLINKS.txt (rewrite com.termux -> our package).
 * (7) Rename staging to prefix.
 */
public final class TermuxInstaller {

    private static final String LOG_TAG = "TermuxInstaller";

    private static File sPrefixDir;
    private static File sStagingDir;
    private static String sStagingPrefixPath;

    private static synchronized void initPaths(Context context) {
        if (sPrefixDir == null) {
            File filesDir = context.getFilesDir();
            sPrefixDir = new File(filesDir, "usr");
            sStagingDir = new File(filesDir, "usr-staging");
            sStagingPrefixPath = sStagingDir.getAbsolutePath();
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

        new Thread() {
            @Override
            public void run() {
                try {
                    Log.i(LOG_TAG, "Installing bootstrap packages.");

                    File stagingDir = sStagingDir;
                    File prefixDir = sPrefixDir;
                    String stagingPath = sStagingPrefixPath;
                    String prefixPath = prefixDir.getAbsolutePath();

                    // Delete prefix staging directory or any file at its destination
                    deleteRecursive(stagingDir);

                    // Delete prefix directory or any file at its destination
                    deleteRecursive(prefixDir);

                    // Create staging directory
                    if (!stagingDir.mkdirs()) {
                        if (!stagingDir.exists()) {
                            throw new RuntimeException("Failed to create staging directory: " + stagingPath);
                        }
                    }

                    final byte[] buffer = new byte[8096];
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
                                    if (parts.length != 2)
                                        throw new RuntimeException("Malformed symlink line: " + line);

                                    // Bootstrap archives are built with our package name, so symlink
                                    // targets are already correct. Use parts[0] as-is like AndroidIDE.
                                    // Also handle legacy AndroidIDE bootstraps with com.itsaky.androidide paths.
                                    String oldPath = parts[0]
                                        .replace("/data/data/com.itsaky.androidide/files/usr", prefixPath)
                                        .replace("/data/user/0/com.itsaky.androidide/files/usr", prefixPath)
                                        .replace("/data/data/com.termux/files/usr", prefixPath)
                                        .replace("/data/user/0/com.termux/files/usr", prefixPath);
                                    String newPath = stagingPath + "/" + parts[1];
                                    symlinks.add(Pair.create(oldPath, newPath));

                                    File parent = new File(newPath).getParentFile();
                                    if (parent != null && !parent.exists() && !parent.mkdirs()) {
                                        throw new RuntimeException("Failed to create directory: " + parent.getAbsolutePath());
                                    }
                                }
                            } else {
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
                                    // Match AndroidIDE exactly: chmod bin/, libexec, lib/apt/*
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

                    Log.i(LOG_TAG, "Moving prefix staging to prefix directory.");

                    // Move staging to final — AndroidIDE does NOT fallback to copy, just rename
                    if (!stagingDir.renameTo(prefixDir)) {
                        throw new RuntimeException("Moving prefix staging to prefix directory failed");
                    }

                    Log.i(LOG_TAG, "Bootstrap packages installed successfully.");

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
            }
        }.start();
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
        new Thread() {
            public void run() {
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
            }
        }.start();
    }

    public static byte[] loadZipBytes() {
        System.loadLibrary("termux-bootstrap");
        return getZip();
    }

    public static native byte[] getZip();
}

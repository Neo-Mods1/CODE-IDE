/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║                    CODE-IDE • NeoMods                      ║
 * ║                  Advanced Android IDE Project              ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 *  (っ◔◡◔)っ ♥
 *
 *  Developer         • NeoMods
 *  Telegram Contact  • @NeoModsDev
 *  Telegram Channel  • https://t.me/NeoModsChannel
 *
 * ──────────────────────────────────────────────────────────────
 *  PROJECT NOTICE
 * ──────────────────────────────────────────────────────────────
 *
 *  This source file is part of the CODE-IDE project.
 *
 *  Unauthorized copying, extraction, redistribution,
 *  mirroring, downloading, modification, or reuse of
 *  CODE-IDE source files is NOT permitted without
 *  explicit permission from the developer.
 *
 *  The application may expose certain components in
 *  read-only mode for educational or preview purposes,
 *  however this DOES NOT grant permission to reuse
 *  or redistribute the source code.
 *
 *  If you need access to the original source code,
 *  implementation details, licensing, or collaboration,
 *  please contact the developer directly.
 *
 *  © NeoMods — All Rights Reserved
 * ──────────────────────────────────────────────────────────────
 */

package com.neo.ide.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public final class IDEEnvironment {

    private static final String TAG = "IDEEnvironment";

    public static File ROOT;
    public static File PREFIX;
    public static File HOME;
    public static File ANDROIDIDE_HOME;
    public static File ANDROIDIDE_UI;
    public static File JAVA_HOME;
    public static File ANDROID_HOME;
    public static File TMP_DIR;
    public static File BIN_DIR;
    public static File LIB_DIR;
    public static File PROJECTS_DIR;
    public static File ANDROID_JAR;
    public static File TOOLING_API_JAR;
    public static File INIT_SCRIPT;
    public static File GRADLE_USER_HOME;
    public static File AAPT2;
    public static File JAVA;
    public static File BASH_SHELL;
    public static File LOGIN_SHELL;

    public static void init(Context context) {
        ROOT = context.getFilesDir();
        PREFIX = mkdirIfNotExists(new File(ROOT, "usr"));
        HOME = mkdirIfNotExists(new File(ROOT, "home"));
        ANDROIDIDE_HOME = mkdirIfNotExists(new File(HOME, ".codeide"));
        TMP_DIR = mkdirIfNotExists(new File(PREFIX, "tmp"));
        BIN_DIR = mkdirIfNotExists(new File(PREFIX, "bin"));
        LIB_DIR = mkdirIfNotExists(new File(PREFIX, "lib"));
        PROJECTS_DIR = mkdirIfNotExists(new File(getExternalStorageDir(), "CODE-IDE-Projects"));
        ANDROID_JAR = new File(ANDROIDIDE_HOME, "android.jar");
        TOOLING_API_JAR = new File(
                mkdirIfNotExists(new File(ANDROIDIDE_HOME, "tooling-api")),
                "tooling-api-all.jar"
        );
        AAPT2 = new File(ANDROIDIDE_HOME, "aapt2");
        ANDROIDIDE_UI = mkdirIfNotExists(new File(ANDROIDIDE_HOME, "ui"));
        INIT_SCRIPT = new File(
                mkdirIfNotExists(new File(ANDROIDIDE_HOME, "init")),
                "init.gradle"
        );
        GRADLE_USER_HOME = new File(HOME, ".gradle");
        ANDROID_HOME = new File(HOME, "android-sdk");
        JAVA_HOME = new File(PREFIX, "opt/openjdk");
        JAVA = new File(JAVA_HOME, "bin/java");
        BASH_SHELL = new File(BIN_DIR, "bash");
        LOGIN_SHELL = new File(BIN_DIR, "login");

        setExecutable(JAVA);
        setExecutable(BASH_SHELL);

        System.setProperty("user.home", HOME.getAbsolutePath());
    }

    public static File mkdirIfNotExists(File dir) {
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static void setExecutable(File file) {
        if (file != null && !file.setExecutable(true)) {
            Log.w(TAG, "Unable to set executable permissions: " + file.getAbsolutePath());
        }
    }

    public static File getExternalStorageDir() {
        File external = Environment.getExternalStorageDirectory();
        if (external != null && external.exists()) {
            return external;
        }
        return new File("/storage/emulated/0");
    }

    public static File createTempFile() {
        File file = newTempFile();
        while (file.exists()) {
            file = newTempFile();
        }
        return file;
    }

    private static File newTempFile() {
        return new File(TMP_DIR, "temp_" + UUID.randomUUID().toString().replace('-', 'X'));
    }
}

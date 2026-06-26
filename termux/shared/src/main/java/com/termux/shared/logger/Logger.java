/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.termux.shared.logger;

import android.util.Log;

public class Logger {

    public static final int LOG_LEVEL_OFF = 0;
    public static final int LOG_LEVEL_NORMAL = 1;
    public static final int LOG_LEVEL_DEBUG = 2;
    public static final int LOG_LEVEL_VERBOSE = 3;

    private static int CURRENT_LOG_LEVEL = LOG_LEVEL_NORMAL;

    public static void setLogLevel(int level) { CURRENT_LOG_LEVEL = level; }
    public static int getLogLevel() { return CURRENT_LOG_LEVEL; }

    public static void logError(String tag, String message) {
        if (CURRENT_LOG_LEVEL >= LOG_LEVEL_NORMAL) Log.e(tag, message);
    }

    public static void logWarn(String tag, String message) {
        if (CURRENT_LOG_LEVEL >= LOG_LEVEL_NORMAL) Log.w(tag, message);
    }

    public static void logInfo(String tag, String message) {
        if (CURRENT_LOG_LEVEL >= LOG_LEVEL_NORMAL) Log.i(tag, message);
    }

    public static void logDebug(String tag, String message) {
        if (CURRENT_LOG_LEVEL >= LOG_LEVEL_DEBUG) Log.d(tag, message);
    }

    public static void logVerbose(String tag, String message) {
        if (CURRENT_LOG_LEVEL >= LOG_LEVEL_VERBOSE) Log.v(tag, message);
    }

    public static void logStackTraceWithMessage(String tag, String message, Exception e) {
        if (CURRENT_LOG_LEVEL >= LOG_LEVEL_NORMAL) Log.e(tag, message, e);
    }

    public static void logStackTrace(String tag, Exception e) {
        if (CURRENT_LOG_LEVEL >= LOG_LEVEL_NORMAL) Log.e(tag, "Stack trace", e);
    }
}

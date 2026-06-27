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



package com.neo.ide.preferences.internal

/**
 * @author Akash Yadav
 */
@Suppress("MemberVisibilityCanBePrivate")
object BuildPreferences {

  const val STACKTRACE = "idepref_gradleCmd_stacktrace"
  const val DEBUG = "idepref_gradleCmd_debug"
  const val SCAN = "idepref_gradleCmd_scan"
  const val INFO = "idepref_gradleCmd_info"
  const val WARNING_MODE = "idepref_gradleCmd_warningMode"
  const val BUILD_CACHE = "idepref_gradleCmd_buildCache"
  const val OFFLINE_MODE = "idepref_gradleCmd_offlineMode"

  const val GRADLE_COMMANDS = "idepref_build_gradleCommands"
  const val GRADLE_CLEAR_CACHE = "idepref_build_gradleClearCache"
  const val CUSTOM_GRADLE_INSTALLATION = "idepref_build_customGradleInstallation"
  const val LAUNCH_APP_AFTER_INSTALL = "ide.build.run.launchAppAfterInstall"
  const val PREF_JAVA_HOME = "ide.build.javaHome"

  /** Switch for Gradle `--debug` option. */
  var isDebugEnabled: Boolean
    get() = prefManager.getBoolean(DEBUG)
    set(enabled) {
      prefManager.putBoolean(DEBUG, enabled)
    }

  /** Switch for Gradle `--scan` option. */
  var isScanEnabled: Boolean
    get() = prefManager.getBoolean(SCAN)
    set(enabled) {
      prefManager.putBoolean(SCAN, enabled)
    }

  /** Switch for Gradle `--warning-mode all` option. */
  var isWarningModeAllEnabled: Boolean
    get() = prefManager.getBoolean(WARNING_MODE)
    set(enabled) {
      prefManager.putBoolean(WARNING_MODE, enabled)
    }

  /** Switch for Gradle `--build-cache` option. */
  var isBuildCacheEnabled: Boolean
    get() = prefManager.getBoolean(BUILD_CACHE)
    set(enabled) {
      prefManager.putBoolean(BUILD_CACHE, enabled)
    }

  /** Switch for Gradle `--offline` option. */
  var isOfflineEnabled: Boolean
    get() = prefManager.getBoolean(OFFLINE_MODE)
    set(enabled) {
      prefManager.putBoolean(OFFLINE_MODE, enabled)
    }

  /** Switch for Gradle `--stacktrace` option. */
  var isStacktraceEnabled: Boolean
    get() = prefManager.getBoolean(STACKTRACE)
    set(value) {
      prefManager.putBoolean(STACKTRACE, value)
    }

  /** Switch for Gradle `--info` option. */
  var isInfoEnabled: Boolean
    get() = prefManager.getBoolean(INFO, GeneralPreferences.isFirstBuild)
    set(enabled) {
      prefManager.putBoolean(INFO, enabled)
    }

  /** Custom Gradle installation directory path. */
  var gradleInstallationDir: String
    get() = prefManager.getString(CUSTOM_GRADLE_INSTALLATION, "")
    set(value) {
      prefManager.putString(CUSTOM_GRADLE_INSTALLATION, value)
    }

  /**
   * Whether the app should be launched automatically after installation (after build).
   */
  var launchAppAfterInstall: Boolean
    get() = prefManager.getBoolean(LAUNCH_APP_AFTER_INSTALL, false)
    set(value) {
      prefManager.putBoolean(LAUNCH_APP_AFTER_INSTALL, value)
    }

  /**
   * The selected Java installation.
   */
  var javaHome: String
    get() = prefManager.getString(PREF_JAVA_HOME, "")
    set(value) {
      prefManager.putString(PREF_JAVA_HOME, value)
    }
}
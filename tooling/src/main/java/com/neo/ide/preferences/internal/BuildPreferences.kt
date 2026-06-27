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

object BuildPreferences {

  private val prefs = mutableMapOf<String, Any>()

  val isStacktraceEnabled: Boolean
    get() = prefs["stacktrace"] as? Boolean ?: false

  val isInfoEnabled: Boolean
    get() = prefs["info"] as? Boolean ?: false

  val isDebugEnabled: Boolean
    get() = prefs["debug"] as? Boolean ?: false

  val isScanEnabled: Boolean
    get() = prefs["scan"] as? Boolean ?: false

  val isWarningModeAllEnabled: Boolean
    get() = prefs["warning_mode_all"] as? Boolean ?: false

  val isBuildCacheEnabled: Boolean
    get() = prefs["build_cache"] as? Boolean ?: false

  val isOfflineEnabled: Boolean
    get() = prefs["offline"] as? Boolean ?: false

  val gradleInstallationDir: String
    get() = prefs["gradle_installation_dir"] as? String ?: ""

  @JvmStatic
  fun setPref(key: String, value: Any) {
    prefs[key] = value
  }
}

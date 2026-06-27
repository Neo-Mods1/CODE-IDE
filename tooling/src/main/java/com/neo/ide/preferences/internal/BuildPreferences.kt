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

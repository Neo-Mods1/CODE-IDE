package com.neo.ide.preferences.internal

object DevOpsPreferences {

  private val prefs = mutableMapOf<String, Any>()

  val logsenderEnabled: Boolean
    get() = prefs["logsender_enabled"] as? Boolean ?: false

  @JvmStatic
  fun setPref(key: String, value: Any) {
    prefs[key] = value
  }
}

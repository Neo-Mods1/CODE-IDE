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
object StatPreferences {

  const val STAT_COLLECTION_CONSENT_SHOWN = "ide.stats.consentShown"
  const val STAT_OPT_IN = "ide.stats.optIn"
  const val STAT_LAST_REPORTED = "ide.stats.lastReported"

  var statConsentDialogShown: Boolean
    get() = prefManager.getBoolean(STAT_COLLECTION_CONSENT_SHOWN, false)
    set(value) {
      prefManager.putBoolean(STAT_COLLECTION_CONSENT_SHOWN, value)
    }

  var statOptIn: Boolean
    get() = prefManager.getBoolean(STAT_OPT_IN, true)
    set(value) {
      prefManager.putBoolean(STAT_OPT_IN, value)
    }

  var statLastReported: Long
    get() = prefManager.getLong(STAT_LAST_REPORTED, 0L)
    set(value) {
      prefManager.putLong(STAT_LAST_REPORTED, value)
    }
}
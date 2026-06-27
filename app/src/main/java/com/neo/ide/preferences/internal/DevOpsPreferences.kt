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
object DevOpsPreferences {

  const val KEY_DEVOPTS = "ide.prefs.developerOptions"
  const val KEY_DEVOPTS_DEBUGGING = "${KEY_DEVOPTS}.debugging"
  const val KEY_DEVOPTS_DEBUGGING_DUMPLOGS = "${KEY_DEVOPTS_DEBUGGING}.dumpLogs"
  const val KEY_DEVOPTS_DEBUGGING_ENABLE_LOGSENDER = "${KEY_DEVOPTS_DEBUGGING}.enableLogsender"

  var dumpLogs: Boolean
    get() = prefManager.getBoolean(KEY_DEVOPTS_DEBUGGING_DUMPLOGS, false)
    set(value) {
      prefManager.putBoolean(KEY_DEVOPTS_DEBUGGING_DUMPLOGS, value)
    }

  var logsenderEnabled: Boolean
    get() = prefManager.getBoolean(KEY_DEVOPTS_DEBUGGING_ENABLE_LOGSENDER, true)
    set(value) {
      prefManager.putBoolean(KEY_DEVOPTS_DEBUGGING_ENABLE_LOGSENDER, value)
    }
}
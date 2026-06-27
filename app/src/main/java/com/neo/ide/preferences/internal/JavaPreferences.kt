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
object JavaPreferences {

  const val GOOGLE_CODE_STYLE = "idepref_editor_java_googleCodeStyle"
  const val JAVA_DIAGNOSTICS_ENABLED = "idepref_editor_java_diagnosticsEnabled"

  var googleCodeStyle: Boolean
    get() = prefManager.getBoolean(GOOGLE_CODE_STYLE, false)
    set(value) {
      prefManager.putBoolean(GOOGLE_CODE_STYLE, value)
    }

  /** Whether diagnostics are enabled for the Java source files. */
  var isJavaDiagnosticsEnabled: Boolean
    get() = prefManager.getBoolean(JAVA_DIAGNOSTICS_ENABLED, true)
    set(value) {
      prefManager.putBoolean(JAVA_DIAGNOSTICS_ENABLED, value)
    }

}
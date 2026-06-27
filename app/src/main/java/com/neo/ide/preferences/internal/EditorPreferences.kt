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
 * Preferences for the code editor.
 *
 * @author Akash Yadav
 */
@Suppress("MemberVisibilityCanBePrivate")
object EditorPreferences {

  const val COMPLETIONS_MATCH_LOWER = "idepref_editor_completions_matchLower"

  const val FLAG_WS_LEADING = "idepref_editor_wsLeading"
  const val FLAG_WS_TRAILING = "idepref_editor_wsTrailing"
  const val FLAG_WS_INNER = "idepref_editor_wsInner"
  const val FLAG_WS_EMPTY_LINE = "idepref_editor_wsEmptyLine"
  const val FLAG_LINE_BREAK = "idepref_editor_lineBreak"
  const val FONT_SIZE = "idepref_editor_fontSize"
  const val PRINTABLE_CHARS = "idepref_editor_nonPrintableFlags"
  const val TAB_SIZE = "idepref_editor_tabSize"
  const val AUTO_SAVE = "idepref_editor_autoSave"
  const val FONT_LIGATURES = "idepref_editor_fontLigatures"
  const val FLAG_PASSWORD = "idepref_editor_flagPassword"
  const val WORD_WRAP = "idepref_editor_word_wrap"
  const val USE_MAGNIFER = "idepref_editor_use_magnifier"
  const val USE_ICU = "idepref_editor_useIcu"
  const val USE_SOFT_TAB = "idepref_editor_useSoftTab"
  const val USE_CUSTOM_FONT = "idepref_editor_useCustomFont"
  const val DELETE_EMPTY_LINES = "idepref_editor_deleteEmptyLines"
  const val DELETE_TABS_ON_BACKSPACE = "idepref_editor_deleteTab"
  const val STICKY_SCROLL_ENABLED = "idepref_editor_stickyScrollEnabled"
  const val PIN_LINE_NUMBERS = "idepref_editor_pinLineNumbers"

  const val COLOR_SCHEME = "idepref_editor_colorScheme"
  const val DEFAULT_COLOR_SCHEME = "default"

  var completionsMatchLower: Boolean
    get() = prefManager.getBoolean(COMPLETIONS_MATCH_LOWER, false)
    set(value) {
      prefManager.putBoolean(COMPLETIONS_MATCH_LOWER, value)
    }

  var drawLeadingWs: Boolean
    get() = prefManager.getBoolean(FLAG_WS_LEADING, false)
    set(value) {
      prefManager.putBoolean(FLAG_WS_LEADING, value)
    }

  var drawTrailingWs: Boolean
    get() = prefManager.getBoolean(FLAG_WS_TRAILING, false)
    set(value) {
      prefManager.putBoolean(FLAG_WS_TRAILING, value)
    }

  var drawInnerWs: Boolean
    get() = prefManager.getBoolean(FLAG_WS_INNER, false)
    set(value) {
      prefManager.putBoolean(FLAG_WS_INNER, value)
    }

  var drawEmptyLineWs: Boolean
    get() = prefManager.getBoolean(FLAG_WS_EMPTY_LINE, false)
    set(value) {
      prefManager.putBoolean(FLAG_WS_EMPTY_LINE, value)
    }

  var drawLineBreak: Boolean
    get() = prefManager.getBoolean(FLAG_LINE_BREAK, false)
    set(value) {
      prefManager.putBoolean(FLAG_LINE_BREAK, value)
    }

  var fontSize: Float
    get() = prefManager.getFloat(FONT_SIZE, 14f)
    set(value) {
      prefManager.putFloat(FONT_SIZE, value)
    }

  var tabSize: Int
    get() = prefManager.getInt(TAB_SIZE, 4)
    set(value) {
      prefManager.putInt(TAB_SIZE, value)
    }

  var autoSave: Boolean
    get() = prefManager.getBoolean(AUTO_SAVE, false)
    set(value) {
      prefManager.putBoolean(AUTO_SAVE, value)
    }

  var fontLigatures: Boolean
    get() = prefManager.getBoolean(FONT_LIGATURES, true)
    set(value) {
      prefManager.putBoolean(FONT_LIGATURES, value)
    }

  var visiblePasswordFlag: Boolean
    get() = prefManager.getBoolean(FLAG_PASSWORD, true)
    set(value) {
      prefManager.putBoolean(FLAG_PASSWORD, value)
    }

  var wordwrap: Boolean
    get() = prefManager.getBoolean(WORD_WRAP, false)
    set(value) {
      prefManager.putBoolean(WORD_WRAP, value)
    }

  var useMagnifier: Boolean
    get() = prefManager.getBoolean(USE_MAGNIFER, true)
    set(value) {
      prefManager.putBoolean(USE_MAGNIFER, value)
    }

  var useIcu: Boolean
    get() = prefManager.getBoolean(USE_ICU, false)
    set(value) {
      prefManager.putBoolean(USE_ICU, value)
    }

  var useSoftTab: Boolean
    get() = prefManager.getBoolean(USE_SOFT_TAB, true)
    set(value) {
      prefManager.putBoolean(USE_SOFT_TAB, value)
    }

  var useCustomFont: Boolean
    get() = prefManager.getBoolean(USE_CUSTOM_FONT, false)
    set(value) {
      prefManager.putBoolean(USE_CUSTOM_FONT, value)
    }

  var colorScheme: String
    get() = prefManager.getString(COLOR_SCHEME, DEFAULT_COLOR_SCHEME)
    set(value) {
      prefManager.putString(COLOR_SCHEME, value)
    }

  var deleteEmptyLines: Boolean
    get() = prefManager.getBoolean(DELETE_EMPTY_LINES, true)
    set(value) {
      prefManager.putBoolean(DELETE_EMPTY_LINES, value)
    }

  var deleteTabsOnBackspace: Boolean
    get() = prefManager.getBoolean(DELETE_TABS_ON_BACKSPACE, true)
    set(value) {
      prefManager.putBoolean(DELETE_TABS_ON_BACKSPACE, value)
    }

  var stickyScrollEnabled: Boolean
    get() = prefManager.getBoolean(STICKY_SCROLL_ENABLED, false)
    set(value) {
      prefManager.putBoolean(STICKY_SCROLL_ENABLED, value)
    }

  var pinLineNumbers: Boolean
    get() = prefManager.getBoolean(PIN_LINE_NUMBERS, true)
    set(value) = prefManager.putBoolean(PIN_LINE_NUMBERS, value)
}
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

import androidx.appcompat.app.AppCompatDelegate
import com.neo.ide.resources.localization.LocaleProvider

/**
 * @author Akash Yadav
 */
@Suppress("MemberVisibilityCanBePrivate")
object GeneralPreferences {

  const val IS_FIRST_PROJECT_BUILD = "project_isFirstBuild"
  const val UI_MODE = "idepref_general_uiMode"
  const val SELECTED_THEME = "idpref_general_theme"
  const val SELECTED_LOCALE = "idpref_general_locale"
  const val OPEN_PROJECTS = "idepref_general_autoOpenProjects"
  const val CONFIRM_PROJECT_OPEN = "idepref_general_confirmProjectOpen"
  const val TERMINAL_USE_SYSTEM_SHELL = "idepref_general_terminalShell"
  const val LAST_OPENED_PROJECT = "ide_last_project"

  const val NO_OPENED_PROJECT = "<NO_OPENED_PROJECT>"

  var uiMode: Int
    get() = prefManager.getInt(UI_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    set(value) {
      prefManager.putInt(UI_MODE, value)
    }

  var selectedTheme: String?
    get() = prefManager.getString(SELECTED_THEME, null)
    set(value) {
      prefManager.putString(SELECTED_THEME, value)
    }

  var selectedLocale: String?
    get() = prefManager.getString(SELECTED_LOCALE, null).let { locale ->

      // if the locale is set to a locale key that is not supported,
      // fall back to 'System default'
      if (LocaleProvider.getLocale(locale) == null) {
        null
      } else {
        locale
      }
    }
    set(value) {
      prefManager.putString(SELECTED_LOCALE, value)
    }

  var isFirstBuild: Boolean
    get() = prefManager.getBoolean(IS_FIRST_PROJECT_BUILD, true)
    set(value) {
      prefManager.putBoolean(IS_FIRST_PROJECT_BUILD, value)
    }

  var autoOpenProjects: Boolean
    get() = prefManager.getBoolean(OPEN_PROJECTS, true)
    set(value) {
      prefManager.putBoolean(OPEN_PROJECTS, value)
    }

  var confirmProjectOpen: Boolean
    get() = prefManager.getBoolean(CONFIRM_PROJECT_OPEN, false)
    set(value) {
      prefManager.putBoolean(CONFIRM_PROJECT_OPEN, value)
    }

  var useSystemShell: Boolean
    get() = prefManager.getBoolean(TERMINAL_USE_SYSTEM_SHELL, false)
    set(value) {
      prefManager.putBoolean(TERMINAL_USE_SYSTEM_SHELL, value)
    }

  var lastOpenedProject: String
    get() = prefManager.getString(LAST_OPENED_PROJECT, NO_OPENED_PROJECT)
    set(value) {
      prefManager.putString(LAST_OPENED_PROJECT, value)
    }


}
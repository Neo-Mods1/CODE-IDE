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



package com.neo.ide.ui.themes

import android.app.Activity
import com.google.auto.service.AutoService
import com.neo.ide.preferences.internal.GeneralPreferences
import com.neo.ide.utils.isSystemInDarkMode

/**
 * Theme manager for AndroidIDE.
 *
 * @author Akash Yadav
 */
@Suppress("unused")
@AutoService(IThemeManager::class)
class ThemeManager : IThemeManager {

  /**
   * Apply the current theme to the given activity. Does nothing if theme is set to [Material You][IDETheme.MATERIAL_YOU].
   */
  override fun applyTheme(activity: Activity) {

    val theme = getCurrentTheme()
    if (theme == IDETheme.MATERIAL_YOU) {
      // No need to apply Material You theme
      return
    }

    val style = if (activity.isSystemInDarkMode()) {
      theme.styleDark
    } else {
      theme.styleLight
    }

    activity.setTheme(style)
  }

  /**
   * Get the currently selected theme.
   */
  override fun getCurrentTheme(): IDETheme {
    return GeneralPreferences.selectedTheme?.let { IDETheme.valueOf(it) } ?: IDETheme.DEFAULT
  }
}
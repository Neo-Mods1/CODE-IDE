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

package com.neo.ide.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class HomeScreenAction(
    val id: Int,
    @StringRes val text: Int,
    @DrawableRes val icon: Int,
    var onClick: ((HomeScreenAction) -> Unit)? = null
) {
    companion object {
        const val ACTION_CREATE_PROJECT = 0
        const val ACTION_OPEN_PROJECT = 1
        const val ACTION_PREFERENCES = 2
    }

    fun all(): List<HomeScreenAction> = listOf(
        HomeScreenAction(ACTION_CREATE_PROJECT, com.neo.ide.R.string.home_action_create_project, com.neo.ide.R.drawable.ic_home_add),
        HomeScreenAction(ACTION_OPEN_PROJECT, com.neo.ide.R.string.home_action_open_project, com.neo.ide.R.drawable.ic_home_folder),
        HomeScreenAction(ACTION_PREFERENCES, com.neo.ide.R.string.home_action_preferences, com.neo.ide.R.drawable.ic_home_settings)
    )
}

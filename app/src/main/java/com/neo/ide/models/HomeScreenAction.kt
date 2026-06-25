/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
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
        const val ACTION_OPEN_TERMINAL = 2
        const val ACTION_PREFERENCES = 3
    }

    fun all(): List<HomeScreenAction> = listOf(
        HomeScreenAction(ACTION_CREATE_PROJECT, com.neo.ide.R.string.home_action_create_project, com.neo.ide.R.drawable.ic_home_add),
        HomeScreenAction(ACTION_OPEN_PROJECT, com.neo.ide.R.string.home_action_open_project, com.neo.ide.R.drawable.ic_home_folder),
        HomeScreenAction(ACTION_OPEN_TERMINAL, com.neo.ide.R.string.home_action_terminal, com.neo.ide.R.drawable.ic_home_terminal),
        HomeScreenAction(ACTION_PREFERENCES, com.neo.ide.R.string.home_action_preferences, com.neo.ide.R.drawable.ic_home_settings)
    )
}

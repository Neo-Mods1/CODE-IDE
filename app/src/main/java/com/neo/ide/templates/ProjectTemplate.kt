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

package com.neo.ide.templates

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import java.io.Serializable

data class ProjectTemplate(
    val id: String,
    @StringRes val nameRes: Int,
    @DrawableRes val iconRes: Int,
    val description: String = "",
    val supportsJava: Boolean = true,
    val supportsKotlin: Boolean = true,
    val hasActivity: Boolean = true,
    val language: TemplateLanguage = TemplateLanguage.JAVA,
    val activityType: ActivityType = ActivityType.EMPTY
) : Serializable {
    enum class TemplateLanguage(val displayName: String, val ext: String) {
        JAVA("Java", "java"),
        KOTLIN("Kotlin", "kt")
    }

    enum class ActivityType(val displayName: String) {
        EMPTY("Empty Activity"),
        BASIC("Basic Activity"),
        NAV_DRAWER("Navigation Drawer"),
        BOTTOM_NAV("Bottom Navigation"),
        TABBED("Tabbed Activity"),
        NONE("No Activity"),
        COMPOSE("Jetpack Compose")
    }

    companion object {
        fun defaults(): List<ProjectTemplate> = listOf(
            ProjectTemplate(
                id = "empty_activity",
                nameRes = com.neo.ide.R.string.tpl_empty_activity,
                iconRes = com.neo.ide.R.drawable.ic_home_add,
                description = "A minimal activity with a single layout file",
                activityType = ActivityType.EMPTY
            ),
            ProjectTemplate(
                id = "basic_activity",
                nameRes = com.neo.ide.R.string.tpl_basic_activity,
                iconRes = com.neo.ide.R.drawable.ic_home_add,
                description = "An activity with a toolbar and a content layout",
                activityType = ActivityType.BASIC
            ),
            ProjectTemplate(
                id = "nav_drawer",
                nameRes = com.neo.ide.R.string.tpl_nav_drawer,
                iconRes = com.neo.ide.R.drawable.ic_home_folder,
                description = "A navigation drawer activity with menu items",
                activityType = ActivityType.NAV_DRAWER
            ),
            ProjectTemplate(
                id = "bottom_nav",
                nameRes = com.neo.ide.R.string.tpl_bottom_nav,
                iconRes = com.neo.ide.R.drawable.ic_home_settings,
                description = "An activity with a bottom navigation bar",
                activityType = ActivityType.BOTTOM_NAV
            ),
            ProjectTemplate(
                id = "tabbed_activity",
                nameRes = com.neo.ide.R.string.tpl_tabbed_activity,
                iconRes = com.neo.ide.R.drawable.ic_home_add,
                description = "An activity with a tab layout and ViewPager",
                activityType = ActivityType.TABBED
            ),
            ProjectTemplate(
                id = "no_activity",
                nameRes = com.neo.ide.R.string.tpl_no_activity,
                iconRes = com.neo.ide.R.drawable.ic_home_folder,
                description = "A project with no activity (library or background service)",
                hasActivity = false,
                activityType = ActivityType.NONE
            ),
            ProjectTemplate(
                id = "empty_activity_kotlin",
                nameRes = com.neo.ide.R.string.tpl_empty_activity_kotlin,
                iconRes = com.neo.ide.R.drawable.ic_home_add,
                description = "A minimal Kotlin activity with a single layout file",
                language = TemplateLanguage.KOTLIN,
                activityType = ActivityType.EMPTY
            ),
            ProjectTemplate(
                id = "compose_activity",
                nameRes = com.neo.ide.R.string.tpl_compose_activity,
                iconRes = com.neo.ide.R.drawable.ic_home_add,
                description = "A Jetpack Compose activity with Material 3",
                language = TemplateLanguage.KOTLIN,
                activityType = ActivityType.COMPOSE,
                supportsJava = false
            )
        )
    }
}

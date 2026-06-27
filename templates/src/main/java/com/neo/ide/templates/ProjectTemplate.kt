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
                nameRes = com.neo.ide.R.string.template_empty,
                iconRes = com.neo.ide.R.drawable.template_empty_activity,
                description = "A minimal activity with a single layout file",
                activityType = ActivityType.EMPTY
            ),
            ProjectTemplate(
                id = "basic_activity",
                nameRes = com.neo.ide.R.string.template_basic,
                iconRes = com.neo.ide.R.drawable.template_basic_activity,
                description = "An activity with a toolbar and a content layout",
                activityType = ActivityType.BASIC
            ),
            ProjectTemplate(
                id = "nav_drawer",
                nameRes = com.neo.ide.R.string.template_navigation_drawer,
                iconRes = com.neo.ide.R.drawable.template_blank_activity_drawer,
                description = "A navigation drawer activity with menu items",
                activityType = ActivityType.NAV_DRAWER
            ),
            ProjectTemplate(
                id = "bottom_nav",
                nameRes = com.neo.ide.R.string.template_navigation_tabs,
                iconRes = com.neo.ide.R.drawable.template_bottom_navigation_activity,
                description = "An activity with a bottom navigation bar",
                activityType = ActivityType.BOTTOM_NAV
            ),
            ProjectTemplate(
                id = "tabbed_activity",
                nameRes = com.neo.ide.R.string.template_tabbed,
                iconRes = com.neo.ide.R.drawable.template_blank_activity_tabs,
                description = "An activity with a tab layout and ViewPager",
                activityType = ActivityType.TABBED
            ),
            ProjectTemplate(
                id = "no_activity",
                nameRes = com.neo.ide.R.string.template_no_activity,
                iconRes = com.neo.ide.R.drawable.template_no_activity,
                description = "A project with no activity (library or background service)",
                hasActivity = false,
                activityType = ActivityType.NONE
            ),
            ProjectTemplate(
                id = "compose_activity",
                nameRes = com.neo.ide.R.string.template_compose,
                iconRes = com.neo.ide.R.drawable.template_compose_empty_activity,
                description = "A Jetpack Compose activity with Material 3",
                language = TemplateLanguage.KOTLIN,
                activityType = ActivityType.COMPOSE,
                supportsJava = false
            )
        )
    }
}

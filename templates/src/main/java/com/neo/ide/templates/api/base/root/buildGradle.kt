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

/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.neo.ide.templates.api.base.root

import com.neo.ide.templates.api.Language
import com.neo.ide.templates.api.base.ProjectTemplateBuilder

internal fun ProjectTemplateBuilder.buildGradleSrcKts(): String {
  return """
    // Top-level build file where you can add configuration options common to all sub-projects/modules.
    plugins {
        id("com.android.application") version "${data.version.gradlePlugin}" apply false
        id("com.android.library") version "${data.version.gradlePlugin}" apply false
        ${ktPlugin()}     
    }

    tasks.register<Delete>("clean") {
        delete(rootProject.buildDir)
    }
  """.trimIndent()
}

internal fun ProjectTemplateBuilder.buildGradleSrcGroovy(): String {
  return """
    // Top-level build file where you can add configuration options common to all sub-projects/modules.
    plugins {
        id 'com.android.application' version '${data.version.gradlePlugin}' apply false
        id 'com.android.library' version '${data.version.gradlePlugin}' apply false
        ${ktPlugin()}     
    }

    task clean(type: Delete) {
        delete rootProject.buildDir
    }
  """.trimIndent()
}

private fun ProjectTemplateBuilder.ktPlugin() = if (data.language == Language.Kotlin) {
  if (data.useKts) ktPluginKts() else ktPluginGroovy()
} else ""

private fun ProjectTemplateBuilder.ktPluginKts(): String {
  return """id("org.jetbrains.kotlin.android") version "${data.version.kotlin}" apply false"""
}

private fun ProjectTemplateBuilder.ktPluginGroovy(): String {
  return "id 'org.jetbrains.kotlin.android' version '${data.version.kotlin}' apply false"
}

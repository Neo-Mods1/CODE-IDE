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

package com.neo.ide.templates.impl.composeActivity

import com.neo.ide.templates.api.Language.Kotlin
import com.neo.ide.templates.api.ProjectVersionData
import com.neo.ide.templates.api.base.composeDependencies
import com.neo.ide.templates.api.base.modules.android.defaultAppModule
import com.neo.ide.templates.api.base.util.AndroidModuleResManager.ResourceType.VALUES
import com.neo.ide.templates.impl.R
import com.neo.ide.templates.impl.base.createRecipe
import com.neo.ide.templates.impl.base.writeMainActivity
import com.neo.ide.templates.impl.baseProjectImpl
import com.neo.ide.templates.api.projectLanguageParameter

private const val composeKotlinVersion = "1.7.20"

private fun composeLanguageParameter() = projectLanguageParameter {
  default = Kotlin
  filter = { it == Kotlin }
}

// Compose template is available only in Kotlin
fun composeActivityProject() =
  baseProjectImpl(language = composeLanguageParameter(),
    projectVersionData = ProjectVersionData(kotlin = composeKotlinVersion)) {

    templateName = R.string.template_compose
    thumb = R.drawable.template_compose_empty_activity

    defaultAppModule(addAndroidX = false) {

      isComposeModule = true

      recipe = createRecipe {

        require(
          data.language == Kotlin) { "Compose activity requires Kotlin language" }

        composeDependencies()

        res {
          writeXmlResource("themes", VALUES, source = ::composeThemesXml)
        }

        sources {
          writeMainActivity(this, ktSrc = ::composeActivitySrc,
            javaSrc = { "" })
          writeKtSrc("${data.packageName}.ui.theme", "Color",
            source = ::themeColorSrc)
          writeKtSrc("${data.packageName}.ui.theme", "Theme",
            source = ::themeThemeSrc)
          writeKtSrc("${data.packageName}.ui.theme", "Type",
            source = ::themeTypeSrc)
        }
      }
    }
  }
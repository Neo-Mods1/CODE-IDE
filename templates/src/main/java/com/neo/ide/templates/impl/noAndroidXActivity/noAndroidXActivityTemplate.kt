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

package com.neo.ide.templates.impl.noAndroidXActivity

import com.android.aaptcompiler.ConfigDescription
import com.android.aaptcompiler.android.ResTableConfig
import com.neo.ide.templates.api.base.modules.android.defaultAppModule
import com.neo.ide.templates.api.base.util.AndroidModuleResManager.ResourceType.LAYOUT
import com.neo.ide.templates.api.base.util.AndroidModuleResManager.ResourceType.VALUES
import com.neo.ide.templates.impl.R
import com.neo.ide.templates.impl.base.createRecipe
import com.neo.ide.templates.impl.base.emptyValuesFile
import com.neo.ide.templates.impl.base.writeMainActivity
import com.neo.ide.templates.impl.baseProjectImpl

fun noAndroidXActivityProject() = baseProjectImpl {
  templateName = R.string.template_no_AndroidX
  thumb = R.drawable.template_empty_noandroidx
  val configNight = ConfigDescription().apply {
    uiMode = ResTableConfig.UI_MODE.NIGHT_YES
  }
  defaultAppModule(addAndroidX = false) {

    // do not set a theme resource to the application
    manifest.themeRes = ""

    recipe = createRecipe {
      res {
        // values
        writeXmlResource("colors", VALUES, source = emptyValuesFile())
        writeXmlResource("themes", VALUES, source = emptyValuesFile())

        // values-night
        writeXmlResource("colors", VALUES, config = configNight,
          source = emptyValuesFile())
        writeXmlResource("themes", VALUES, config = configNight,
          source = emptyValuesFile())

        writeXmlResource("activity_main", LAYOUT,
          source = noAndroidXActivityLayout())
      }

      sources {
        writeMainActivity(this, ktSrc = ::noAndroidXActivitySrcKt,
          javaSrc = ::noAndroidXActivitySrcJava)
      }
    }
  }
}

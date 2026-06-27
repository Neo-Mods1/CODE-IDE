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

package com.neo.ide.templates.api.base.modules.android

import com.neo.ide.templates.api.ModuleTemplate
import com.neo.ide.templates.api.base.AndroidModuleTemplateBuilder
import com.neo.ide.templates.api.base.AndroidModuleTemplateConfigurator
import com.neo.ide.templates.api.base.ProjectTemplateBuilder
import com.neo.ide.templates.api.base.baseAndroidXDependencies
import com.neo.ide.templates.api.base.util.AndroidManifestBuilder.ConfigurationType.APPLICATION_ATTR

/**
 * Configure the default template for the project.
 *
 * @param name The name of the module (gradle format, e.g. ':app').
 * @param copyDefAssets Whether to copy the default Android assets (except `values` directory) to this module.
 * @param block The module configurator.
 */
inline fun ProjectTemplateBuilder.defaultAppModule(name: String = ":app",
                                            addAndroidX: Boolean = true,
                                            copyDefAssets: Boolean = true,
                                            crossinline block: AndroidModuleTemplateConfigurator
) {
  check(
    defModuleTemplate == null) { "Default module has been already configured" }

  val module = AndroidModuleTemplateBuilder().apply {
    _name = name
    templateName = 0
    thumb = 0

    preRecipe = commonPreRecipe {
      return@commonPreRecipe defModule
    }

    postRecipe = commonPostRecipe {
      if (copyDefAssets) {
        copyDefaultRes()

        // add manifest attributes for data extraction rules
        // and backup rules
        manifest {
          configure(APPLICATION_ATTR) {
            androidAttribute("dataExtractionRules",
              "@xml/data_extraction_rules")

            androidAttribute("fullBackupContent", "@xml/backup_rules")
          }
        }
      }
    }

    if (addAndroidX) {
      baseAndroidXDependencies()
    }

    block()
  }.build() as ModuleTemplate

  modules.add(module)
}
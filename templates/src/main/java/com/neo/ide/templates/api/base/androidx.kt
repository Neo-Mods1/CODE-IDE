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

package com.neo.ide.templates.api.base

import com.neo.ide.templates.api.base.models.Dependency

/**
 * Configures the template to use AndroidX and Material Design Components dependencies.
 */
fun AndroidModuleTemplateBuilder.baseAndroidXDependencies() {
  addDependency(Dependency.AndroidX.AppCompat)
  addDependency(Dependency.AndroidX.ConstraintLayout)
  addDependency(Dependency.Google.Material)
}

fun AndroidModuleTemplateBuilder.composeDependencies() {
  addDependency(Dependency.AndroidX.Compose.Core_Ktx)
  addDependency(Dependency.AndroidX.Compose.LifeCycle_Runtime_Ktx)
  addDependency(Dependency.AndroidX.Compose.Activity)

  addDependency(dependency = Dependency.AndroidX.Compose.BOM, isPlatform = true)
  addDependency(Dependency.AndroidX.Compose.UI)
  addDependency(Dependency.AndroidX.Compose.UI_Graphics)
  addDependency(Dependency.AndroidX.Compose.UI_Tooling_Preview)
  addDependency(Dependency.AndroidX.Compose.Material3)
  addDependency(Dependency.AndroidX.Compose.UI_Tooling)
  addDependency(Dependency.AndroidX.Compose.UI_Test_Manifest)
}
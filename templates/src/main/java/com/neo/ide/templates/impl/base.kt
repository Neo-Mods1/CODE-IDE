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

package com.neo.ide.templates.impl

import com.neo.ide.templates.api.BooleanParameter
import com.neo.ide.templates.api.EnumParameter
import com.neo.ide.templates.api.Language
import com.neo.ide.templates.api.ProjectTemplate
import com.neo.ide.templates.api.ProjectVersionData
import com.neo.ide.templates.api.Sdk
import com.neo.ide.templates.api.StringParameter
import com.neo.ide.templates.api.base.AndroidModuleTemplateBuilder
import com.neo.ide.templates.api.base.ProjectTemplateBuilder
import com.neo.ide.templates.api.base.baseProject
import com.neo.ide.templates.impl.base.createRecipe
import com.neo.ide.templates.api.minSdkParameter
import com.neo.ide.templates.api.packageNameParameter
import com.neo.ide.templates.api.projectLanguageParameter
import com.neo.ide.templates.api.projectNameParameter
import com.neo.ide.templates.api.useKtsParameter

/**
 * Indents the given string for the given [indentation level][level].
 */
fun String.indentToLevel(level: Int): String {
  val lines = split(Regex("[\r\n]"))
  return StringBuilder().apply {
    for (line in lines) {
      append(line)
      append(" ".repeat(level * 4))
    }
  }.toString()
}

@Suppress("UnusedReceiverParameter")
internal fun AndroidModuleTemplateBuilder.templateAsset(name: String,
                                                        path: String
): String {
  return "templates/${name}/${path}"
}

internal inline fun baseProjectImpl(
  projectName: StringParameter = projectNameParameter(),
  packageName: StringParameter = packageNameParameter(),
  useKts: BooleanParameter = useKtsParameter(),
  minSdk: EnumParameter<Sdk> = minSdkParameter(),
  language: EnumParameter<Language> = projectLanguageParameter(),
  projectVersionData: ProjectVersionData = ProjectVersionData(),
  crossinline block: ProjectTemplateBuilder.() -> Unit
): ProjectTemplate =
  baseProject(projectName = projectName, packageName = packageName,
    useKts = useKts, minSdk = minSdk, language = language,
    projectVersionData = projectVersionData) {
    block()

    // make sure we return a proper result
    if (!isRecipeSet) {
      recipe = createRecipe {}
    }
  }
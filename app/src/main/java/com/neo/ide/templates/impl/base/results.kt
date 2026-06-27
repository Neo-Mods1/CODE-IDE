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

package com.neo.ide.templates.impl.base
import com.neo.ide.templates.api.ModuleTemplateData
import com.neo.ide.templates.api.ModuleTemplateRecipeResult
import com.neo.ide.templates.api.ProjectTemplateData
import com.neo.ide.templates.api.ProjectTemplateRecipeResult
import com.neo.ide.templates.api.base.ModuleTemplateBuilder
import com.neo.ide.templates.api.base.ProjectTemplateBuilder
data class ProjectTemplateRecipeResultImpl(
  override val data: ProjectTemplateData
) : ProjectTemplateRecipeResult
data class ModuleTemplateRecipeResultImpl(override val data: ModuleTemplateData
) : ModuleTemplateRecipeResult
internal fun ProjectTemplateBuilder.recipeResult(): ProjectTemplateRecipeResult {
  return ProjectTemplateRecipeResultImpl(data)
}
internal fun ModuleTemplateBuilder.recipeResult(): ModuleTemplateRecipeResult {
  return ModuleTemplateRecipeResultImpl(data)
}

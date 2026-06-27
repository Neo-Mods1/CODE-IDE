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

package com.neo.ide.tooling.impl.sync

import com.neo.ide.builder.model.IJavaCompilerSettings
import com.neo.ide.tooling.api.IJavaProject
import com.neo.ide.tooling.api.messages.InitializeProjectParams
import com.neo.ide.tooling.api.models.JavaModuleCompilerSettings
import com.neo.ide.tooling.impl.internal.JavaProjectImpl
import org.gradle.tooling.model.idea.IdeaModule
import org.gradle.tooling.model.idea.IdeaProject

/**
 * Builds model for Java library projects.
 *
 * @author Akash Yadav
 */
class JavaProjectModelBuilder(initializationParams: InitializeProjectParams) :
  AbstractModelBuilder<JavaProjectModelBuilderParams, IJavaProject>(initializationParams) {

  override fun build(param: JavaProjectModelBuilderParams): IJavaProject {
    val compilerSettings = createCompilerSettings(param.project, param.module)
    return JavaProjectImpl(param.module, compilerSettings, param.modulePaths)
  }

  private fun createCompilerSettings(
    ideaProject: IdeaProject, module: IdeaModule): IJavaCompilerSettings {
    val javaLanguageSettings = module.javaLanguageSettings
      ?: return createCompilerSettings(ideaProject)
    val languageLevel = javaLanguageSettings.languageLevel
    val targetBytecodeVersion = javaLanguageSettings.targetBytecodeVersion
    if (languageLevel == null || targetBytecodeVersion == null) {
      return createCompilerSettings(ideaProject)
    }
    val source = languageLevel.toString()
    val target = targetBytecodeVersion.toString()
    return JavaModuleCompilerSettings(source, target)
  }

  private fun createCompilerSettings(ideaProject: IdeaProject): IJavaCompilerSettings {
    val settings = ideaProject.javaLanguageSettings ?: return JavaModuleCompilerSettings()
    val source = settings.languageLevel
    val target = settings.targetBytecodeVersion
    return if (source == null || target == null) {
      JavaModuleCompilerSettings()
    } else JavaModuleCompilerSettings(source.toString(), target.toString())
  }
}

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

import com.android.builder.model.v2.models.AndroidDsl
import com.android.builder.model.v2.models.AndroidProject
import com.android.builder.model.v2.models.BasicAndroidProject
import com.android.builder.model.v2.models.ModelBuilderParameter
import com.android.builder.model.v2.models.ProjectSyncIssues
import com.android.builder.model.v2.models.VariantDependencies
import com.neo.ide.tooling.api.IAndroidProject
import com.neo.ide.tooling.api.messages.InitializeProjectParams
import com.neo.ide.tooling.impl.internal.AndroidProjectImpl

/**
 * Builds model for Android application and library projects.
 *
 * @author Akash Yadav
 */
class AndroidProjectModelBuilder(initializationParams: InitializeProjectParams) :
  AbstractModelBuilder<AndroidProjectModelBuilderParams, IAndroidProject>(initializationParams) {

  override fun build(param: AndroidProjectModelBuilderParams): IAndroidProject {
    val (controller, module, versions, syncIssueReporter) = param

    val androidParams = initializationParams.androidParams
    val projectPath = module.gradleProject.path
    val basicModel = controller.getModelAndLog(module, BasicAndroidProject::class.java)
    val androidModel = controller.getModelAndLog(module, AndroidProject::class.java)
    val androidDsl = controller.getModelAndLog(module, AndroidDsl::class.java)

    val variantNames = basicModel.variants.map { it.name }
    log(
      "${variantNames.size} build variants found for project '$projectPath': $variantNames")

    var androidVariant = androidParams.variantSelections[projectPath]

    if (androidVariant != null && !variantNames.contains(androidVariant)) {
      log(
        "Configured variant '$androidVariant' not found for project '$projectPath'. Falling back to default variant.")
      androidVariant = null
    }

    val configurationVariant = androidVariant ?: variantNames.firstOrNull()
    if (configurationVariant.isNullOrBlank()) {
      throw ModelBuilderException(
        "No variant found for project '$projectPath'. providedVariant=$androidVariant")
    }

    log("Selected build variant '$configurationVariant' for project '$projectPath'")

    val variantDependencies = controller.getModelAndLog(module, VariantDependencies::class.java,
      ModelBuilderParameter::class.java) {
      it.variantName = configurationVariant
      it.dontBuildRuntimeClasspath = false
      it.dontBuildAndroidTestRuntimeClasspath = true
      it.dontBuildTestFixtureRuntimeClasspath = true
      it.dontBuildUnitTestRuntimeClasspath = true
      it.dontBuildHostTestRuntimeClasspath = emptyMap()
      it.dontBuildScreenshotTestRuntimeClasspath = true
    }

    controller.findModel(module, ProjectSyncIssues::class.java)?.also { syncIssues ->
      syncIssueReporter.reportAll(syncIssues)
    }

    return AndroidProjectImpl(
      module.gradleProject,
      configurationVariant,
      basicModel,
      androidModel,
      variantDependencies,
      versions,
      androidDsl
    )
  }
}

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

import com.neo.ide.builder.model.DefaultProjectSyncIssues
import com.neo.ide.builder.model.DefaultSyncIssue
import com.neo.ide.builder.model.shouldBeIgnored
import com.neo.ide.tooling.api.IAndroidProject
import com.neo.ide.tooling.api.IProject
import com.neo.ide.tooling.api.messages.InitializeProjectParams
import com.neo.ide.tooling.api.util.AndroidModulePropertyCopier
import com.neo.ide.tooling.impl.Main
import com.neo.ide.tooling.impl.Main.finalizeLauncher
import com.neo.ide.tooling.impl.internal.ProjectImpl
import org.gradle.tooling.ConfigurableLauncher
import org.gradle.tooling.model.idea.IdeaProject
import org.slf4j.LoggerFactory
import java.io.Serializable

/**
 * Utility class to build the project models.
 *
 * @author Akash Yadav
 */
class RootModelBuilder(initializationParams: InitializeProjectParams) :
  AbstractModelBuilder<RootProjectModelBuilderParams, IProject>(initializationParams),
  Serializable {

  private val serialVersionUID = 1L

  override fun build(param: RootProjectModelBuilderParams): IProject {

    val (projectConnection, cancellationToken) = param

    // do not reference the 'initializationParams' field in the
    val initializationParams = initializationParams

    val executor = projectConnection.action { controller ->
      val ideaProject = controller.getModelAndLog(IdeaProject::class.java)

      val ideaModules = ideaProject.modules
      val modulePaths = mapOf(*ideaModules.map { it.name to it.gradleProject.path }.toTypedArray())
      val rootModule = ideaModules.find { it.gradleProject.parent == null }
        ?: throw ModelBuilderException(
          "Unable to find root project")

      val rootProjectVersions = getAndroidVersions(rootModule, controller)

      val syncIssues = hashSetOf<DefaultSyncIssue>()
      val syncIssueReporter = ISyncIssueReporter {
        if (it.shouldBeIgnored()) {
          // this SyncIssue should not be shown to the user
          return@ISyncIssueReporter
        }

        val issue = it as? DefaultSyncIssue ?: AndroidModulePropertyCopier.copy(it)
        syncIssues.add(issue)
      }

      val rootProject = if (rootProjectVersions != null) {
        // Root project is an Android project
        checkAgpVersion(rootProjectVersions, syncIssueReporter)
        AndroidProjectModelBuilder(initializationParams)
          .build(AndroidProjectModelBuilderParams(
            controller,
            rootModule,
            rootProjectVersions,
            syncIssueReporter
          ))
      } else {
        GradleProjectModelBuilder(initializationParams).build(rootModule.gradleProject)
      }

      val projects = ideaModules.map { ideaModule ->
        ModuleProjectModelBuilder(initializationParams).build(
          ModuleProjectModelBuilderParams(
            controller,
            ideaProject,
            ideaModule,
            modulePaths,
            syncIssueReporter
          ))
      }

      return@action ProjectImpl(
        rootProject,
        rootModule.gradleProject.path,
        projects,
        DefaultProjectSyncIssues(syncIssues)
      )
    }

    finalizeLauncher(executor)
    applyAndroidModelBuilderProps(executor)

    if (cancellationToken != null) {
      executor.withCancellationToken(cancellationToken)
    }

    val logger = LoggerFactory.getLogger("RootModelBuilder")
    logger.warn("Starting build. See build output for more details...")

    if (Main.client != null) {
      Main.client.logOutput("Starting build...")
    }

    return executor.run().also {
      logger.debug("Build action executed. Result: {}", it)
    }
  }

  private fun applyAndroidModelBuilderProps(
    launcher: ConfigurableLauncher<*>) {
    launcher.addProperty(IAndroidProject.PROPERTY_BUILD_MODEL_ONLY, true)
    launcher.addProperty(IAndroidProject.PROPERTY_INVOKED_FROM_IDE, true)
  }

  private fun ConfigurableLauncher<*>.addProperty(property: String, value: Any) {
    addArguments(String.format("-P%s=%s", property, value))
  }
}

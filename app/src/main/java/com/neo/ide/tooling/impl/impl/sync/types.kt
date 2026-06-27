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

import com.android.builder.model.v2.models.Versions
import org.gradle.tooling.BuildController
import org.gradle.tooling.CancellationToken
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.model.idea.IdeaModule
import org.gradle.tooling.model.idea.IdeaProject

/**
 * Parameters for the root project model builder.
 *
 * @property projectConnection The project connection
 * @property cancellationToken The cancellation token.
 */
data class RootProjectModelBuilderParams(
  val projectConnection: ProjectConnection,
  val cancellationToken: CancellationToken?
)

/**
 * Parameters for building model for an Android project.
 *
 * @property controller The build controller that will be used to fetch project models.
 * @property module The [IdeaModule] to fetch the models from.
 * @property versions The Android Gradle Plugin version information.
 * @property syncIssueReporter [ISyncIssueReporter] to report project synchronization issues.
 */
data class AndroidProjectModelBuilderParams(
  val controller: BuildController,
  val module: IdeaModule,
  val versions: Versions,
  val syncIssueReporter: ISyncIssueReporter
)

class ModuleProjectModelBuilderParams(
  val controller: BuildController,
  project: IdeaProject,
  module: IdeaModule,
  modulePaths: Map<String, String>,
  val syncIssueReporter: ISyncIssueReporter
) : JavaProjectModelBuilderParams(
  project, module, modulePaths)

open class JavaProjectModelBuilderParams(val project: IdeaProject, val module: IdeaModule,
  val modulePaths: Map<String, String>) {

  constructor(base: ModuleProjectModelBuilderParams) : this(base.project, base.module,
    base.modulePaths)
}

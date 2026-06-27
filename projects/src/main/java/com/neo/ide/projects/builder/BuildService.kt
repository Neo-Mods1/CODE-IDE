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

package com.neo.ide.projects.builder

import com.neo.ide.lookup.Lookup
import com.neo.ide.lookup.Lookup.Key
import com.neo.ide.tooling.api.IProject
import com.neo.ide.tooling.api.messages.InitializeProjectParams
import com.neo.ide.tooling.api.messages.result.BuildCancellationRequestResult
import com.neo.ide.tooling.api.messages.result.InitializeResult
import com.neo.ide.tooling.api.messages.result.TaskExecutionResult
import com.neo.ide.tooling.api.models.ToolingServerMetadata
import java.util.concurrent.CompletableFuture

/**
 * A build service provides API to initialize project, execute builds, query a build, cancel running
 * builds, etc.
 *
 * @author Akash Yadav
 */
interface BuildService {

  companion object {

    /** Key that can be used to retrieve the [BuildService] instance using the [Lookup] API. */
    @JvmField
    val KEY_BUILD_SERVICE = Key<BuildService>()

    /**
     * Key that can be used to retrieve the instance of Tooling API's [IProject] model using the
     * [Lookup] API.
     */
    @JvmField
    val KEY_PROJECT_PROXY = Key<IProject>()
  }

  /** Whether a build is in progress or not. */
  val isBuildInProgress: Boolean

  /** Returns `true` if and only if the tooling API server has been started, `false` otherwise. */
  fun isToolingServerStarted(): Boolean

  /**
   * Returns the [ToolingServerMetadata] of the tooling API server.
   */
  fun metadata(): CompletableFuture<ToolingServerMetadata>

  /**
   * Initialize the project.
   *
   * @param params Parameters for the project initialization.
   * @return A [CompletableFuture] which returns an [InitializeResult] when the project
   *   initialization process finishes.
   */
  fun initializeProject(params: InitializeProjectParams): CompletableFuture<InitializeResult>

  /**
   * Execute the given tasks.
   *
   * @param tasks The tasks to execute. If the fully qualified path of the task is not specified,
   *   then it will be executed in the root project directory.
   * @return A [CompletableFuture] which returns a list of [TaskExecutionResult]. The result
   *   contains a list of tasks that were executed and the result of the whole execution.
   */
  fun executeTasks(vararg tasks: String): CompletableFuture<TaskExecutionResult>

  /** Cancel any running build. */
  fun cancelCurrentBuild(): CompletableFuture<BuildCancellationRequestResult>
}

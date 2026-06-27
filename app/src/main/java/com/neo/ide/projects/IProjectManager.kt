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

package com.neo.ide.projects

import androidx.annotation.RestrictTo
import com.android.builder.model.v2.models.ProjectSyncIssues
import com.neo.ide.lookup.Lookup
import com.neo.ide.projects.builder.BuildService
import com.neo.ide.tooling.api.IProject
import com.neo.ide.utils.ServiceLoader
import java.io.File

/**
 * Project manager.
 *
 * @author Akash Yadav
 */
interface IProjectManager {

  companion object {

    private var projectManager: IProjectManager? = null

    /**
     * Get the project manager instance.
     */
    @JvmStatic
    fun getInstance(): IProjectManager {
      return projectManager ?: ServiceLoader.load(IProjectManager::class.java).findFirstOrThrow()
        .also {
          projectManager = it
        }
    }
  }

  /**
   * The path to the project's root directory.
   */
  val projectDirPath: String
    get() = projectDir.path

  /**
   * The project's root directory.
   */
  val projectDir: File

  /**
   * Issues that were encountered during project synchronization.
   */
  val projectSyncIssues: ProjectSyncIssues?

  /**
   * Open the given project directory.
   */
  fun openProject(directory: File)

  /**
   * Same as [openProject].
   */
  fun openProject(path: String) = openProject(File(path))

  /**
   * Setup the project with the given [project proxy][project] from the Tooling API.
   *
   * @param project The project proxy.
   */
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
  suspend fun setupProject(
    project: IProject = Lookup.getDefault().lookup(BuildService.KEY_PROJECT_PROXY)!!
  )

  /**
   * Get the workspace instance.
   *
   * @return The configured workspace, or `null`.
   */
  fun getWorkspace(): IWorkspace?

  /**
   * Get the workspace instance.
   *
   * @return The configured workspace.
   * @throws IWorkspace.NotConfiguredException If the workspace has not been configured yet.
   */
  fun requireWorkspace(): IWorkspace = getWorkspace() ?: throw IWorkspace.NotConfiguredException()

  /**
   * Notify the project manager that the given <code>file</code> was created.
   * @param file The file that was created.
   */
  fun notifyFileCreated(file: File)

  /**
   * Notify the project manager that the given <code>file</code> was deleted.
   * @param file The file that was deleted.
   */
  fun notifyFileDeleted(file: File)

  /**
   * Notify the project manager that the file was renamed or moved.
   * @param from The file that was renamed or moved.
   * @param to The file after renaming/move.
   */
  fun notifyFileRenamed(from: File, to: File)

  /**
   * Destroy the project manager.
   */
  fun destroy()
}

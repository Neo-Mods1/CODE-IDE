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

import com.android.builder.model.v2.models.ProjectSyncIssues
import com.neo.ide.projects.android.AndroidModule
import com.neo.ide.tooling.api.models.BuildVariantInfo
import java.io.File
import java.nio.file.Path

/**
 * Workspace represents everything related to the project opened in the IDE.
 *
 * @author Akash Yadav
 */
interface IWorkspace {

  // TODO: Add support for composite (included) Gradle builds

  /**
   * Get the project directory for the workspace. This is usually the root project directory.
   */
  fun getProjectDir(): File

  /**
   * Get the root project model.
   */
  fun getRootProject(): GradleProject

  /**
   * Get the subprojects included in the root project.
   */
  fun getSubProjects(): List<GradleProject>

  /**
   * Get the issues that were encountered while synchronizing the project with Gradle files.
   */
  fun getProjectSyncIssues(): ProjectSyncIssues

  /**
   * Get the build variants that are selected (configured/synchronized) for each of the Android modules
   * in this workspace. The keys in the returned map are the paths of the module projects and the values
   * are the information about the project's variants.
   */
  fun getAndroidVariantSelections(): Map<String, BuildVariantInfo>

  /**
   * Finds the project by the given path.
   *
   * @return The project with the given path or `null` if no project is available with that path.
   */
  fun findProject(path: String): GradleProject?

  /**
   * Get the project with the given project path.
   *
   * @param path The project path.
   * @return The project with the given path.
   * @throws ProjectNotFoundException If the project could not be found.
   */
  fun getProject(path: String): GradleProject =
    findProject(path) ?: throw ProjectNotFoundException(path)

  /**
   * List all the [AndroidModule]s in this project. If this project is itself an Android module,
   * then it also added to the list at index `0`.
   *
   * @return A sequence of android modules.
   */
  fun androidProjects(): Sequence<AndroidModule>

  /**
   * Find the module project for the given file path.
   *
   * @param file The file to find the module for.
   * @param checkExistance Whether to check if the file exists or not.
   * @return The [ModuleProject] if found, `null` otherwise.
   */
  fun findModuleForFile(file: Path, checkExistance: Boolean = false): ModuleProject?

  /**
   * Find the module project for the given file path.
   *
   * @param file The file to find the module for.
   * @param checkExistance Whether to check if the file exists or not.
   * @return The [ModuleProject] if found, `null` otherwise.
   */
  fun findModuleForFile(file: File, checkExistance: Boolean = false): ModuleProject?

  /**
   * Check if any of the module projects contain the given [file] in their source folder.
   *
   * @param file The file to check.
   * @return `true` if the given [file] is a source file in any of the mdoules, `false` otherwise.
   */
  fun containsSourceFile(file: Path): Boolean

  /**
   * Checks if the given file is a resource file in any of the included Android modules.
   *
   * @param file The file to check.
   * @return `true` if the given file is a resource file in any of the Android modules, `false` otherwise.
   */
  fun isAndroidResource(file: File): Boolean

  /**
   * Thrown by [IWorkspace] if the project with a given path could not be found in the workspace.
   */
  class ProjectNotFoundException(path: String) :
    RuntimeException("Could not find project with path: $path")

  /**
   * Thrown by [IProjectManager] when trying to access the workspace and it is not configured yet.
   */
  class NotConfiguredException() : RuntimeException("Workspace not configured")
}

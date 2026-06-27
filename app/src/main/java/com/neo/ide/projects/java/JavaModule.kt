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

package com.neo.ide.projects.java

import com.neo.ide.builder.model.IJavaCompilerSettings
import com.neo.ide.projects.IProjectManager
import com.neo.ide.projects.ModuleProject
import com.neo.ide.tooling.api.ProjectType.Java
import com.neo.ide.tooling.api.models.GradleTask
import com.neo.ide.tooling.api.models.JavaContentRoot
import com.neo.ide.tooling.api.models.JavaModuleDependency
import com.neo.ide.tooling.api.models.JavaModuleExternalDependency
import com.neo.ide.tooling.api.models.JavaModuleProjectDependency
import java.io.File

/**
 * A [GradleProject] model implementation for Java library modules which is exposed to other modules and
 * provides additional helper methods.
 *
 * @param name The display name of the project.
 * @param description The project description.
 * @param path The project path (same as Gradle project paths). For example, `:app`,
 * `:module:submodule`, etc. Root project is always represented by path `:`.
 * @param projectDir The project directory.
 * @param buildDir The build directory of the project.
 * @param buildScript The Gradle buildscript file of the project.
 * @param tasks The tasks of the project.
 * @param contentRoots The source roots of this module.
 * @param dependencies The dependencies of this module.
 * @author Akash Yadav
 */
class JavaModule(
  name: String,
  description: String,
  path: String,
  projectDir: File,
  buildDir: File,
  buildScript: File,
  tasks: List<GradleTask>,
  override val compilerSettings: IJavaCompilerSettings,
  val contentRoots: List<JavaContentRoot>,
  val dependencies: List<JavaModuleDependency>,
  val classesJar: File?
) :
  ModuleProject(
    name,
    description,
    path,
    projectDir,
    buildDir,
    buildScript,
    tasks
  ) {

  companion object {

    const val SCOPE_COMPILE = "COMPILE"
    const val SCOPE_RUNTIME = "RUNTIME"
  }

  init {
    type = Java
  }

  override fun getClassPaths(): Set<File> {
    return getModuleClasspaths()
  }

  override fun getSourceDirectories(): Set<File> {
    val sources = mutableSetOf<File>()
    contentRoots.forEach {
      sources.addAll(it.sourceDirectories.map { sourceDirectory -> sourceDirectory.directory })
    }
    return sources
  }

  override fun getCompileSourceDirectories(): Set<File> {
    val dirs = getSourceDirectories().toMutableSet()
    getCompileModuleProjects().forEach { dirs.addAll(it.getSourceDirectories()) }
    return dirs
  }

  override fun getModuleClasspaths(): Set<File> {
    return mutableSetOf(classesJar ?: File("does-not-exist.jar"))
  }

  override fun getCompileClasspaths(): Set<File> {
    val classpaths = getModuleClasspaths().toMutableSet()
    getCompileModuleProjects().forEach { classpaths.addAll(it.getCompileClasspaths()) }
    classpaths.addAll(getDependencyClasspaths())
    return classpaths
  }

  override fun getCompileModuleProjects(): List<ModuleProject> {
    val workspace = IProjectManager.getInstance().getWorkspace() ?: return emptyList()
    return this.dependencies
      .filterIsInstance<JavaModuleProjectDependency>()
      .filter { it.scope == SCOPE_COMPILE }
      .mapNotNull { workspace.findProject(it.projectPath) }
      .filterIsInstance<ModuleProject>()
  }

  fun getDependencyClasspaths(): Set<File> {
    return this.dependencies.filterIsInstance<JavaModuleExternalDependency>()
      .mapNotNull { it.jarFile }.toHashSet()
  }
}

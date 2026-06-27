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

package com.neo.ide.projects.internal

import com.neo.ide.projects.GradleProject
import com.neo.ide.projects.android.AndroidModule
import com.neo.ide.projects.java.JavaModule
import com.neo.ide.tooling.api.IAndroidProject
import com.neo.ide.tooling.api.IGradleProject
import com.neo.ide.tooling.api.IJavaProject
import com.neo.ide.tooling.api.IProject
import com.neo.ide.tooling.api.ProjectType
import com.neo.ide.tooling.api.models.AndroidProjectMetadata
import com.neo.ide.tooling.api.models.BasicProjectMetadata
import com.neo.ide.tooling.api.models.JavaProjectMetadata
import com.neo.ide.tooling.api.models.params.StringParameter
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Transforms project models from tooling API to the projects API.
 *
 * @author Akash Yadav
 */
internal object WorkspaceModelBuilder {

  private val log = LoggerFactory.getLogger(WorkspaceModelBuilder::class.java)

  fun build(
    projectDir: File,
    project: IProject
  ): WorkspaceImpl? {
    try {
      val allProjects = project.getProjects().get()
      val selectionResult = project.selectProject(StringParameter("")).get()
      check(selectionResult.isSuccessful) {
        "Cannot find root project"
      }

      val rootProject = when (project.getType().get()) {
        ProjectType.Gradle -> transform(project.asGradleProject())
        ProjectType.Android -> transform(project.asAndroidProject())
        else -> throw IllegalStateException(
          "Root project must be either an Android project or a Gradle project"
        )
      }

      return WorkspaceImpl(
        projectDir,
        rootProject,
        CopyOnWriteArrayList(transform(allProjects, project)),
        project.getProjectSyncIssues().get()
      )
    } catch (error: Throwable) {
      log.error("Unable to transform project", error)
      return null
    }
  }

  private fun transform(rootProject: IGradleProject): GradleProject {
    val metadata = rootProject.getMetadata().get()
    return GradleProject(
      name = metadata.name ?: IProject.PROJECT_UNKNOWN,
      description = metadata.description ?: "",
      path = metadata.projectPath,
      projectDir = metadata.projectDir,
      buildDir = metadata.buildDir,
      buildScript = metadata.buildScript,

      // The list will never change, we could make these thread-safe with
      // CopyOnWriteArrayList
      tasks = CopyOnWriteArrayList(rootProject.getTasks().get() ?: listOf()),
    )
  }

  private fun transform(
    project: IAndroidProject
  ): AndroidModule {
    val metadata = project.getMetadata().get() as AndroidProjectMetadata
    val libraryMap = project.getLibraryMap().get()
    val variants = project.getVariants().get()
    val configuredVariant = project.getConfiguredVariant().get()
    return AndroidModule(
      name = metadata.name ?: IProject.PROJECT_UNKNOWN,
      description = metadata.description ?: "",
      path = metadata.projectPath,
      projectDir = metadata.projectDir,
      buildDir = metadata.buildDir,
      buildScript = metadata.buildScript,
      tasks = project.getTasks().get(),
      resourcePrefix = metadata.resourcePrefix,
      namespace = metadata.namespace,
      androidTestNamespace = metadata.androidTestNamespace,
      testFixtureNamespace = metadata.testFixtureNamespace,
      projectType = metadata.androidType,
      mainSourceSet = project.getMainSourceSet().get(),
      flags = metadata.flags,
      compilerSettings = metadata.javaCompileOptions,
      viewBindingOptions = metadata.viewBindingOptions,
      bootClassPaths = project.getBootClasspaths().get(),
      libraries = libraryMap.keys,
      libraryMap = libraryMap,
      lintCheckJars = project.getLintCheckJars().get(),
      variants = variants,
      configuredVariant = variants.find { it.name == configuredVariant },
      classesJar = metadata.classesJar
    )
  }

  private fun transform(project: IJavaProject): JavaModule {
    val metadata = project.getMetadata().get() as JavaProjectMetadata
    return JavaModule(
      name = metadata.name ?: IProject.PROJECT_UNKNOWN,
      description = metadata.description ?: "",
      path = metadata.projectPath,
      projectDir = metadata.projectDir,
      buildDir = metadata.buildDir,
      buildScript = metadata.buildScript,
      tasks = project.getTasks().get(),
      contentRoots = project.getContentRoots().get(),
      dependencies = project.getDependencies().get(),
      compilerSettings = metadata.compilerSettings,
      classesJar = metadata.classesJar
    )
  }

  private fun transform(modules: List<BasicProjectMetadata>, root: IProject): List<GradleProject> {
    return mutableListOf<GradleProject>().apply {
      for (module in modules) {
        add(createProject(module, root))
      }
    }
  }

  private fun createProject(moduleMetadata: BasicProjectMetadata, root: IProject): GradleProject {
    val selectionResult = root.selectProject(StringParameter(moduleMetadata.projectPath)).get()
    check(selectionResult.isSuccessful) {
      "Selection failed for project '${moduleMetadata.projectPath}' but it is included in all projects."
    }

    val type = root.getType().get() ?: throw java.lang.IllegalStateException("Invalid module data")

    return when (type) {
      ProjectType.Gradle,
      ProjectType.Unknown -> transform(root.asGradleProject())

      ProjectType.Android -> transform(root.asAndroidProject())
      ProjectType.Java -> transform(root.asJavaProject())
    }
  }
}

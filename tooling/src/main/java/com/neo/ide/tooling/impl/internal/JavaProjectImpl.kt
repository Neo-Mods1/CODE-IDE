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

package com.neo.ide.tooling.impl.internal

import com.neo.ide.builder.model.IJavaCompilerSettings
import com.neo.ide.tooling.api.IJavaProject
import com.neo.ide.tooling.api.models.GradleArtifact
import com.neo.ide.tooling.api.models.JavaContentRoot
import com.neo.ide.tooling.api.models.JavaModuleDependency
import com.neo.ide.tooling.api.models.JavaModuleExternalDependency
import com.neo.ide.tooling.api.models.JavaModuleProjectDependency
import com.neo.ide.tooling.api.models.JavaProjectMetadata
import com.neo.ide.tooling.api.models.JavaSourceDirectory
import com.neo.ide.tooling.api.models.ProjectMetadata
import org.gradle.tooling.model.idea.IdeaModule
import org.gradle.tooling.model.idea.IdeaModuleDependency
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency
import java.io.File
import java.io.Serializable
import java.util.concurrent.CompletableFuture

/**
 * @author Akash Yadav
 */
internal class JavaProjectImpl(
  private val ideaModule: IdeaModule,
  private val compilerSettings: IJavaCompilerSettings,
  private var allModulePaths: Map<String, String> = emptyMap()
) : GradleProjectImpl(ideaModule.gradleProject),
  IJavaProject, Serializable {

  private val serialVersionUID = 1L

  override fun getContentRoots(): CompletableFuture<List<JavaContentRoot>> {
    return CompletableFuture.supplyAsync {
      val list = ArrayList<JavaContentRoot>()
      for (contentRoot in ideaModule.contentRoots) {
        val thisRoot = JavaContentRoot()
        for (sourceDir in contentRoot!!.sourceDirectories) {
          (thisRoot.sourceDirectories as MutableList).add(
            JavaSourceDirectory(sourceDir!!.directory, sourceDir.isGenerated))
        }
        for (testDir in contentRoot.testDirectories) {
          (thisRoot.testDirectories as MutableList).add(
            JavaSourceDirectory(testDir!!.directory, testDir.isGenerated))
        }
        list.add(thisRoot)
      }

      return@supplyAsync list
    }
  }

  override fun getDependencies(): CompletableFuture<List<JavaModuleDependency>> {
    return CompletableFuture.supplyAsync {
      val list = ArrayList<JavaModuleDependency>()
      for (dependency in ideaModule.dependencies) {
        // TODO There might be unresolved dependencies here. We need to handle them too.
        if (dependency is IdeaSingleEntryLibraryDependency) {
          val file = dependency.file
          val source = dependency.source
          val javadoc = dependency.javadoc
          val artifact = getGradleArtifact(dependency)
          list.add(
            JavaModuleExternalDependency(
              file,
              source,
              javadoc,
              artifact,
              dependency.getScope().scope,
              dependency.getExported()))
        } else if (dependency is IdeaModuleDependency) {
          val moduleName = dependency.targetModuleName
          list.add(
            JavaModuleProjectDependency(
              moduleName,
              allModulePaths[moduleName] ?: "",
              dependency.scope.scope,
              dependency.exported))
        }
      }

      return@supplyAsync list
    }
  }

  private fun getGradleArtifact(external: IdeaSingleEntryLibraryDependency): GradleArtifact? {
    val moduleVersion = external.gradleModuleVersion ?: return null
    return GradleArtifact(
      moduleVersion.group, moduleVersion.name, moduleVersion.version)
  }

  private fun getClassesJar(): File {
    return getClassesJar(getMetadata().get())
  }

  private fun getClassesJar(metadata: ProjectMetadata): File {
    var jar = File(metadata.buildDir, "libs/${metadata.name}.jar")
    if (jar.exists()) {
      return jar
    }

    jar =
      File(metadata.buildDir, "libs").listFiles()?.firstOrNull { metadata.name?.let(it.name::startsWith) ?: false }
        ?: File("module-jar-does-not-exist.jar")

    return jar
  }

  override fun getClasspaths(): CompletableFuture<List<File>> {
    return CompletableFuture.supplyAsync {
      getDependencies().get().mapNotNull { it.jarFile }.toMutableList().apply { add(getClassesJar()) }
    }
  }

  override fun getMetadata(): CompletableFuture<ProjectMetadata> {
    return CompletableFuture.supplyAsync {
      val base = super.getMetadata().get()

      // do not call getClassesJar() here
      // it'll try to fetch metadata which will in return call this method
      // this will result in an infinite loop
      return@supplyAsync JavaProjectMetadata(base, compilerSettings, getClassesJar(base))
    }
  }
}

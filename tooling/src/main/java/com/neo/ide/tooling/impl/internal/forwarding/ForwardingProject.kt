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

package com.neo.ide.tooling.impl.internal.forwarding

import com.neo.ide.builder.model.DefaultLibrary
import com.neo.ide.builder.model.DefaultSourceSetContainer
import com.neo.ide.tooling.api.IAndroidProject
import com.neo.ide.tooling.api.IGradleProject
import com.neo.ide.tooling.api.IJavaProject
import com.neo.ide.tooling.api.models.AndroidVariantMetadata
import com.neo.ide.tooling.api.models.BasicAndroidVariantMetadata
import com.neo.ide.tooling.api.models.GradleTask
import com.neo.ide.tooling.api.models.JavaContentRoot
import com.neo.ide.tooling.api.models.JavaModuleDependency
import com.neo.ide.tooling.api.models.ProjectMetadata
import com.neo.ide.tooling.api.models.params.StringParameter
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * @author Akash Yadav
 */
@Suppress("NewApi")
internal class ForwardingProject(var project: IGradleProject? = null) : IGradleProject,
  IAndroidProject, IJavaProject {

  private val androidProject: IAndroidProject?
    get() = this.project as? IAndroidProject?

  private val javaProject: IJavaProject?
    get() = this.project as? IJavaProject?


  override fun getContentRoots(): CompletableFuture<List<JavaContentRoot>> {
    return this.javaProject?.getContentRoots() ?: CompletableFuture.failedFuture(
      UnsupportedOperationException())
  }

  override fun getDependencies(): CompletableFuture<List<JavaModuleDependency>> {
    return this.javaProject?.getDependencies() ?: CompletableFuture.failedFuture(
      UnsupportedOperationException())
  }

  override fun getConfiguredVariant(): CompletableFuture<String> {
    return this.androidProject?.getConfiguredVariant() ?: CompletableFuture.failedFuture(
      UnsupportedOperationException()
    )
  }

  override fun getVariants(): CompletableFuture<List<BasicAndroidVariantMetadata>> {
    return this.androidProject?.getVariants() ?: CompletableFuture.failedFuture(
      UnsupportedOperationException())
  }

  override fun getVariant(param: StringParameter): CompletableFuture<AndroidVariantMetadata?> {
    return this.androidProject?.getVariant(param) ?: CompletableFuture.failedFuture(
      UnsupportedOperationException())
  }

  override fun getBootClasspaths(): CompletableFuture<Collection<File>> {
    return this.androidProject?.getBootClasspaths() ?: CompletableFuture.failedFuture(
      UnsupportedOperationException())
  }

  override fun getLibraryMap(): CompletableFuture<Map<String, DefaultLibrary>> {
    return this.androidProject?.getLibraryMap() ?: CompletableFuture.failedFuture(
      UnsupportedOperationException())
  }

  override fun getMainSourceSet(): CompletableFuture<DefaultSourceSetContainer?> {
    return this.androidProject?.getMainSourceSet() ?: CompletableFuture.failedFuture(
      UnsupportedOperationException())
  }

  override fun getLintCheckJars(): CompletableFuture<List<File>> {
    return this.androidProject?.getLintCheckJars() ?: CompletableFuture.failedFuture(
      UnsupportedOperationException())
  }

  override fun getClasspaths(): CompletableFuture<List<File>> {
    return this.javaProject?.getClasspaths() ?: CompletableFuture.failedFuture(
      UnsupportedOperationException())
  }

  override fun getMetadata(): CompletableFuture<ProjectMetadata> {
    return this.project?.getMetadata() ?: CompletableFuture.failedFuture(
      UnsupportedOperationException())
  }

  override fun getTasks(): CompletableFuture<List<GradleTask>> {
    return this.project?.getTasks() ?: CompletableFuture.failedFuture(
      UnsupportedOperationException())
  }
}

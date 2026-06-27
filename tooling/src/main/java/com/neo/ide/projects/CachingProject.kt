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
import androidx.annotation.RestrictTo.Scope.LIBRARY
import com.neo.ide.builder.model.DefaultProjectSyncIssues
import com.neo.ide.tooling.api.IAndroidProject
import com.neo.ide.tooling.api.IGradleProject
import com.neo.ide.tooling.api.IJavaProject
import com.neo.ide.tooling.api.IProject
import com.neo.ide.tooling.api.ProjectType
import com.neo.ide.tooling.api.models.BasicProjectMetadata
import com.neo.ide.tooling.api.models.params.StringParameter
import com.neo.ide.tooling.api.models.result.SelectProjectResult
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

/**
 * A project which lazily caches some required properties of the given project.
 *
 * @author Akash Yadav
 */
@RestrictTo(LIBRARY)
open class CachingProject(val project: IProject) : IProject {

  private val projects = mutableListOf<BasicProjectMetadata>()
  private var syncIssues: DefaultProjectSyncIssues? = null

  companion object {

    private val log = LoggerFactory.getLogger(CachingProject::class.java)
  }

  override fun getProjects(): CompletableFuture<List<BasicProjectMetadata>> {
    return if (this.projects.isNotEmpty()) {
      log.info("Using cached project metadata...")
      CompletableFuture.completedFuture(this.projects)
    } else this.project.getProjects().whenComplete { projects, err ->
      if (err != null || projects == null) {
        log.debug("Unable to fetch project metadata from tooling server", err)
        return@whenComplete
      }

      if (projects.isEmpty()) {
        log.debug("Empty project metadata returned by tooling server. Ignoring...")
        return@whenComplete
      }

      this.projects.clear()
      this.projects.addAll(projects)
    }
  }

  override fun getProjectSyncIssues(): CompletableFuture<DefaultProjectSyncIssues> {
    this.syncIssues?.also {
      return CompletableFuture.completedFuture(it)
    }

    return this.project.getProjectSyncIssues().whenComplete { projectSyncIssues, err ->
      if (err != null || projectSyncIssues == null) {
        log.debug("Unable to fetch project sync issues from tooling server", err)
        return@whenComplete
      }

      if (projectSyncIssues.syncIssues.isEmpty()) {
        log.debug("No sync issues.")
        return@whenComplete
      }

      this.syncIssues = projectSyncIssues
    }
  }

  override fun selectProject(param: StringParameter): CompletableFuture<SelectProjectResult> {
    return project.selectProject(param)
  }

  override fun getType(): CompletableFuture<ProjectType> {
    return project.getType()
  }

  override fun asGradleProject(): IGradleProject {
    return project.asGradleProject()
  }

  override fun asAndroidProject(): IAndroidProject {
    return project.asAndroidProject()
  }

  override fun asJavaProject(): IJavaProject {
    return project.asJavaProject()
  }
}

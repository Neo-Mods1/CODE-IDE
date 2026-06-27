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

import com.neo.ide.builder.model.DefaultProjectSyncIssues
import com.neo.ide.tooling.api.IAndroidProject
import com.neo.ide.tooling.api.IGradleProject
import com.neo.ide.tooling.api.IJavaProject
import com.neo.ide.tooling.api.IProject
import com.neo.ide.tooling.api.ProjectType
import com.neo.ide.tooling.api.models.BasicProjectMetadata
import com.neo.ide.tooling.api.models.params.StringParameter
import com.neo.ide.tooling.api.models.result.SelectProjectResult
import com.neo.ide.tooling.impl.internal.forwarding.ForwardingProject
import java.io.Serializable
import java.util.concurrent.CompletableFuture

/**
 * @author Akash Yadav
 */
internal class ProjectImpl(
  var rootProject: IGradleProject? = null,
  var rootProjectPath: String? = null,
  var projects: List<IGradleProject> = emptyList(),
  var projectSyncIssues: DefaultProjectSyncIssues = DefaultProjectSyncIssues(emptyList())
) : IProject, Serializable {

  private val serialVersionUID = 1L

  @Transient
  private var _lock: Any? = null
  private val lock: Any
    get() = _lock ?: Any().also { _lock = it }

  @Transient
  private val selectedProject: ForwardingProject

  init {
    require((rootProject == null) == (rootProjectPath == null)) {
      "rootProject, rootProjectPath: both must be specified or null"
    }
    this.selectedProject = ForwardingProject()
  }

  fun setFrom(other: ProjectImpl) {
    this.rootProject = other.rootProject
    this.rootProjectPath = other.rootProjectPath
    this.projects = other.projects
    this.projectSyncIssues = other.projectSyncIssues
  }

  private fun getProject(path: String): IGradleProject? {
    return if (path.isBlank()) rootProject else projects.find {
      it.getMetadata().get().projectPath == path
    }
  }

  override fun getProjects(): CompletableFuture<List<BasicProjectMetadata>> {
    return CompletableFuture.supplyAsync {
      projects.map { it.getMetadata().get() }
    }
  }

  override fun getProjectSyncIssues(): CompletableFuture<DefaultProjectSyncIssues> {
    return CompletableFuture.completedFuture(
      this.projectSyncIssues ?: DefaultProjectSyncIssues(emptyList())
    )
  }

  override fun selectProject(param: StringParameter): CompletableFuture<SelectProjectResult> {
    return CompletableFuture.supplyAsync {
      synchronized(lock) {
        this.selectedProject.project = getProject(param.value)
        SelectProjectResult(this.selectedProject.project != null)
      }
    }
  }

  override fun getType(): CompletableFuture<ProjectType> {
    return CompletableFuture.supplyAsync {
      synchronized(lock) {
        return@supplyAsync when (this.selectedProject.project) {
          is IAndroidProject -> ProjectType.Android
          is IJavaProject -> ProjectType.Java
          is IGradleProject -> ProjectType.Gradle
          else -> ProjectType.Unknown
        }
      }
    }
  }

  override fun asGradleProject(): IGradleProject {
    return this.selectedProject
  }

  override fun asAndroidProject(): IAndroidProject {
    return this.selectedProject
  }

  override fun asJavaProject(): IJavaProject {
    return this.selectedProject
  }
}

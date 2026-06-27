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

import androidx.annotation.RestrictTo
import com.android.builder.model.v2.models.ProjectSyncIssues
import com.google.auto.service.AutoService
import com.google.common.collect.ImmutableList
import com.neo.ide.eventbus.events.EventReceiver
import com.neo.ide.eventbus.events.editor.DocumentSaveEvent
import com.neo.ide.eventbus.events.file.FileCreationEvent
import com.neo.ide.eventbus.events.file.FileDeletionEvent
import com.neo.ide.eventbus.events.file.FileEvent
import com.neo.ide.eventbus.events.file.FileRenameEvent
import com.neo.ide.eventbus.events.project.ProjectInitializedEvent
import com.neo.ide.lookup.Lookup
import com.neo.ide.projects.CachingProject
import com.neo.ide.projects.IProjectManager
import com.neo.ide.projects.IWorkspace
import com.neo.ide.projects.ModuleProject
import com.neo.ide.projects.R
import com.neo.ide.projects.android.AndroidModule
import com.neo.ide.projects.builder.BuildService
import com.neo.ide.tasks.executeAsync
import com.neo.ide.tooling.api.IAndroidProject
import com.neo.ide.tooling.api.IProject
import com.neo.ide.tooling.api.messages.result.InitializeResult
import com.neo.ide.tooling.api.models.BuildVariantInfo
import com.neo.ide.utils.DocumentUtils
import com.neo.ide.utils.flashError
import com.neo.ide.utils.withStopWatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.slf4j.LoggerFactory
import java.io.File
import java.util.Locale
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString

/**
 * Internal implementation of [IProjectManager].
 *
 * @author Akash Yadav
 */
@AutoService(IProjectManager::class)
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
class ProjectManagerImpl : IProjectManager, EventReceiver {

  private var _workspace: WorkspaceImpl? = null
  private var _projectDir: File? = null

  var projectInitialized: Boolean = false
  var cachedInitResult: InitializeResult? = null

  override val projectDir: File
    get() = checkNotNull(_projectDir) {
      "Cannot get project directory. Path has not been set."
    }

  override val projectSyncIssues: ProjectSyncIssues?
    get() = getWorkspace()?.getProjectSyncIssues()

  override fun getWorkspace(): IWorkspace? {
    return _workspace
  }

  override fun openProject(directory: File) {
    // IMP: Always use canonical path
    this._projectDir = directory.canonicalFile
  }

  override suspend fun setupProject(project: IProject) {
    this._workspace = withStopWatch("Transform project proxy") {
      withContext(Dispatchers.IO) {
        WorkspaceModelBuilder.build(projectDir, CachingProject(project))
      }
    }

    val rootProject = this.getWorkspace() ?: return

    // build variants must be updated before the sources and classpaths are indexed
    updateBuildVariants { buildVariants ->
      _workspace!!.setVariantSelections(buildVariants)
    }

    log.info(
      "Found {} project sync issues: {}",
      rootProject.getProjectSyncIssues().syncIssues.size,
      rootProject.getProjectSyncIssues().syncIssues
    )

    withStopWatch("Setup project") {
      val indexerScope = CoroutineScope(Dispatchers.Default)
      val modulesFlow = flow {
        rootProject.getSubProjects().filterIsInstance<ModuleProject>().forEach {
          emit(it)
        }
      }

      val jobs = modulesFlow.map { module ->
        indexerScope.async {
          module.indexSourcesAndClasspaths()
          if (module is AndroidModule) {
            module.readResources()
          }
        }
      }

      // wait for the indexing to finish
      jobs.toList().awaitAll()
    }
  }

  override fun destroy() {
    log.info("Destroying project manager")

    this._workspace?.setVariantSelections(emptyMap())
    this._workspace = null

    this._projectDir = null
    this.cachedInitResult = null
    this.projectInitialized = false
  }

  @JvmOverloads
  fun generateSources(
    builder: BuildService? = Lookup.getDefault().lookup(BuildService.KEY_BUILD_SERVICE)
  ) {
    if (builder == null) {
      log.warn("Cannot generate sources. BuildService is null.")
      return
    }

    if (!builder.isToolingServerStarted()) {
      flashError(R.string.msg_tooling_server_unavailable)
      return
    }

    if (builder.isBuildInProgress) {
      return
    }

    val tasks = getWorkspace()?.androidProjects()?.flatMap { module ->
      val variant = module.getSelectedVariant()
      if (variant == null) {
        log.error(
          "Selected build variant for project '{}' not found", module.path
        )
        return@flatMap emptyList()
      }

      val mainArtifact = variant.mainArtifact
      val variantNameCapitalized = variant.name.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
      }

      return@flatMap listOf(
        mainArtifact.resGenTaskName,
        mainArtifact.sourceGenTaskName,
        if (module.viewBindingOptions.isEnabled) "dataBindingGenBaseClasses$variantNameCapitalized" else null,
        "process${variantNameCapitalized}Resources"
      ).mapNotNull { it?.let { "${module.path}:${it}" } }
    }?.toList() ?: emptyList()


    builder.executeTasks(*tasks.toTypedArray()).whenComplete { result, taskErr ->
      if (result == null || !result.isSuccessful || taskErr != null) {
        log.warn(
          "Execution for tasks failed: {} {}", tasks, taskErr ?: ""
        )
      } else {
        notifyProjectUpdate()
      }
    }
  }

  fun notifyProjectUpdate() {

    executeAsync {
      getWorkspace()?.apply {
        getSubProjects().forEach { subproject ->
          if (subproject is ModuleProject) {
            subproject.indexSources()
          }
        }
      }

      val event = ProjectInitializedEvent()
      event.put(IWorkspace::class.java, getWorkspace())
      EventBus.getDefault().post(event)
    }
  }

  private fun updateBuildVariants(onUpdated: (Map<String, BuildVariantInfo>) -> Unit = {}) {
    val rootProject = checkNotNull(this.getWorkspace()) {
      "Cannot update build variants. Root project model is null."
    }

    val buildVariants = mutableMapOf<String, BuildVariantInfo>()
    rootProject.getSubProjects().forEach { subproject ->
      if (subproject is AndroidModule) {

        // variant names are not expected to be modified
        val variantNames = ImmutableList.builder<String>()
          .addAll(subproject.variants.map { variant -> variant.name }).build()

        val variantName = subproject.configuredVariant?.name ?: IAndroidProject.DEFAULT_VARIANT

        buildVariants[subproject.path] =
          BuildVariantInfo(subproject.path, variantNames, variantName)
      }
    }

    onUpdated(buildVariants)
  }

  private fun generateSourcesIfNecessary(event: FileEvent) {
    val builder = Lookup.getDefault().lookup(BuildService.KEY_BUILD_SERVICE) ?: return
    val file = event.file
    if (getWorkspace()?.isAndroidResource(file) != true) {
      return
    }

    generateSources(builder)
  }

  @Suppress("unused")
  @Subscribe(threadMode = ThreadMode.ASYNC)
  fun onFileSaved(event: DocumentSaveEvent) {
    event.file.apply {
      if (isDirectory()) {
        return@apply
      }

      if (extension != "xml") {
        return@apply
      }

      val module = getWorkspace()?.findModuleForFile(this, false) ?: return@apply
      if (module !is AndroidModule) {
        return@apply
      }

      val isResource = module.mainSourceSet?.sourceProvider?.resDirectories?.any {
        this.pathString.contains(it.path)
      } ?: false

      if (isResource) {
        module.updateResourceTable()
      }
    }
  }

  override fun notifyFileCreated(file: File) {
    onFileCreated(FileCreationEvent(file))
  }

  override fun notifyFileDeleted(file: File) {
    onFileDeleted(FileDeletionEvent(file))
  }

  override fun notifyFileRenamed(from: File, to: File) {
    onFileRenamed(FileRenameEvent(from, to))
  }

  @Suppress("unused")
  @Subscribe(threadMode = ThreadMode.BACKGROUND)
  fun onFileCreated(event: FileCreationEvent) {
    generateSourcesIfNecessary(event)

    if (DocumentUtils.isJavaFile(event.file.toPath())) {
      getWorkspace()?.findModuleForFile(event.file, false)?.let {
        val sourceRoot = it.findSourceRoot(event.file) ?: return@let

        // add the source node entry
        it.compileJavaSourceClasses.append(event.file.toPath(), sourceRoot)
      }
    }
  }

  @Suppress("unused")
  @Subscribe(threadMode = ThreadMode.BACKGROUND)
  fun onFileDeleted(event: FileDeletionEvent) {
    generateSourcesIfNecessary(event)

    // Remove the source node entry
    // Do not check for Java file DocumentUtils.isJavaFile(...) as it checks for file existence as
    // well. As the file is already deleted, it will always return false
    if (event.file.extension == "java") {
      getWorkspace()?.findModuleForFile(
        event.file,
        false
      )?.compileJavaSourceClasses?.findSource(event.file.toPath())
        ?.let { it.parent?.removeChild(it) }
    }
  }

  @Suppress("unused")
  @Subscribe(threadMode = ThreadMode.BACKGROUND)
  fun onFileRenamed(event: FileRenameEvent) {
    generateSourcesIfNecessary(event)

    // Do not check for Java file DocumentUtils.isJavaFile(...) as it checks for file existence as
    // well. As the file is already renamed to another filename, it will always return false
    if (event.file.extension == "java") {
      // remove the source node entry
      getWorkspace()?.findModuleForFile(
        event.file,
        false
      )?.compileJavaSourceClasses?.findSource(event.file.toPath())
        ?.let { it.parent?.removeChild(it) }
    }

    if (DocumentUtils.isJavaFile(event.newFile.toPath())) {
      getWorkspace()?.findModuleForFile(event.newFile, false)?.let {
        val sourceRoot = it.findSourceRoot(event.newFile) ?: return@let
        // add the new source node entry
        it.compileJavaSourceClasses.append(event.newFile.toPath(), sourceRoot)
      }
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(ProjectManagerImpl::class.java)

    @JvmStatic
    fun getInstance(): ProjectManagerImpl = IProjectManager.getInstance() as ProjectManagerImpl
  }
}

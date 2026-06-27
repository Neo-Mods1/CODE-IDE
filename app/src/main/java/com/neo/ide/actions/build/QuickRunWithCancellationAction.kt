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



package com.neo.ide.actions.build

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.core.content.ContextCompat
import com.neo.ide.R.string
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.BaseBuildAction
import com.neo.ide.actions.getContext
import com.neo.ide.actions.markInvisible
import com.neo.ide.actions.openApplicationModuleChooser
import com.neo.ide.lookup.Lookup
import com.neo.ide.models.ApkMetadata
import com.neo.ide.projects.android.AndroidModule
import com.neo.ide.projects.builder.BuildService
import com.neo.ide.resources.R
import com.neo.ide.tooling.api.messages.result.TaskExecutionResult
import com.neo.ide.tooling.api.models.BasicAndroidVariantMetadata
import com.neo.ide.utils.ApkInstaller
import com.neo.ide.utils.InstallationResultHandler
import com.neo.ide.utils.flashError
import com.neo.ide.utils.resolveAttr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.File

/**
 * The 'Quick Run' and 'Cancel build' action in the editor activity.
 *
 * If a build is in progress, executing this action cancels the build. Otherwise, the selected
 * build variant is built and installed to the device.
 *
 * @author Akash Yadav
 */
class QuickRunWithCancellationAction(context: Context, override val order: Int) :
  BaseBuildAction() {

  companion object {

    private val log = LoggerFactory.getLogger(QuickRunWithCancellationAction::class.java)
  }

  init {
    label = context.getString(R.string.quick_run_debug)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_run_outline)
    enabled = false
  }

  override val id: String = "ide.editor.build.quickRun"

  // Execute on UI thread as this action might try to show dialogs to the user
  override var requiresUIThread: Boolean = true

  override fun prepare(data: ActionData) {
    super.prepare(data)
    val context = data.getActivity() ?: run {
      markInvisible()
      return
    }

    if (data.isBuildInProgress()) {
      label = context.getString(R.string.title_cancel_build)
      icon = ContextCompat.getDrawable(context, R.drawable.ic_stop_daemons)
    } else {
      label = context.getString(R.string.quick_run_debug)
      icon = ContextCompat.getDrawable(context, R.drawable.ic_run_outline)
    }

    visible = true
    enabled = true
  }

  override fun createColorFilter(data: ActionData): ColorFilter? {
    return data.getContext()?.let {
      PorterDuffColorFilter(it.resolveAttr(
        if (data.isBuildInProgress())
          R.attr.colorError
        else R.attr.colorSuccess
      ), PorterDuff.Mode.SRC_ATOP)
    }
  }

  override suspend fun execAction(data: ActionData): Boolean {
    if (data.isBuildInProgress()) {
      return cancelBuild()
    }

    return quickRun(data)
  }

  private fun quickRun(data: ActionData): Boolean {
    openApplicationModuleChooser(data) { module ->
      val activity = data.requireActivity()

      val variant = module.getSelectedVariant() ?: run {
        activity.flashError(
          activity.getString(R.string.err_selected_variant_not_found))
        return@openApplicationModuleChooser
      }

      val taskName = "${module.path}:${variant.mainArtifact.assembleTaskName}"
      log.info(
        "Running task '$taskName' to assemble variant '${variant.name}' of project '${module.path}'")

      onModuleSelected(data, module, variant, taskName)
    }
    return true
  }

  private fun onModuleSelected(
    data: ActionData,
    module: AndroidModule,
    variant: BasicAndroidVariantMetadata,
    taskName: String
  ) {

    val buildService = this.buildService ?: run {
      log.error("Cannot execute task '{}'. BuildService not found.", taskName)
      return
    }

    if (!buildService.isToolingServerStarted()) {
      flashError(string.msg_tooling_server_unavailable)
      return
    }

    val activity =
      data.getActivity()
        ?: run {
          log.error(
            "Cannot execute task '{}'. Activity instance not provided in ActionData.", taskName)
          return
        }

    actionScope.launch(Dispatchers.Default) {
      activity.saveAllResult()

      val result = withContext(Dispatchers.IO) {
        buildService.executeTasks(taskName).get()
      }

      log.debug("Task execution result: {}", result)

      if (result?.isSuccessful != true) {
        log.error("Tasks failed to execute: '{}'", taskName)
        return@launch
      }

      handleResult(data, result, module, variant)
    }.invokeOnCompletion { error ->
      if (error != null) {
        log.error("Failed to run '{}'", taskName, error)
      }
    }
  }

  private fun cancelBuild(): Boolean {
    log.info("Sending build cancellation request...")
    val builder = Lookup.getDefault().lookup(BuildService.KEY_BUILD_SERVICE)
    if (builder?.isToolingServerStarted() != true) {
      flashError(com.neo.ide.projects.R.string.msg_tooling_server_unavailable)
      return false
    }

    builder.cancelCurrentBuild().whenComplete { result,
      error ->
      if (error != null) {
        log.error("Failed to send build cancellation request", error)
        return@whenComplete
      }

      if (!result.wasEnqueued) {
        log.warn(
          "Unable to enqueue cancellation request reason={} reason.message={}",
          result.failureReason,
          result.failureReason!!.message
        )
        return@whenComplete
      }

      log.info("Build cancellation request was successfully enqueued...")
    }

    return true
  }

  private fun handleResult(
    data: ActionData,
    result: TaskExecutionResult?,
    module: AndroidModule,
    variant: BasicAndroidVariantMetadata
  ) {
    if (result == null || !result.isSuccessful) {
      log.debug("Cannot install APK. Task execution failed.")
      return
    }

    log.debug("Installing APK(s) for project: '{}' variant: '{}'", module.path, variant.name)

    val main = variant.mainArtifact
    val outputListingFile = main.assembleTaskOutputListingFile
    if (outputListingFile == null) {
      log.error("No output listing file provided with project model")
      return
    }

    log.trace("Parsing metadata")
    val apkFile = ApkMetadata.findApkFile(outputListingFile)
    if (apkFile == null) {
      log.error("No apk file specified in output listing file: {}", outputListingFile)
      return
    }

    if (!apkFile.exists()) {
      log.error("APK file specified in output listing file does not exist! {}", apkFile)
      return
    }

    install(data, apkFile)
  }

  private fun install(data: ActionData, apk: File) {
    val activity =
      data.getActivity()
        ?: run {
          log.error("Cannot install APK. Unable to get activity instance.")
          return
        }

    activity.runOnUiThread {
      log.debug("Installing APK: {}", apk)

      if (!apk.exists()) {
        log.error("APK file does not exist!")
        return@runOnUiThread
      }

      ApkInstaller.installApk(
        activity,
        InstallationResultHandler.createEditorActivitySender(activity),
        apk,
        activity.installationSessionCallback()
      )
    }
  }

  private fun ActionData.isBuildInProgress(): Boolean {
    val context = getActivity()
    val buildService = Lookup.getDefault().lookup(BuildService.KEY_BUILD_SERVICE)
    return context?.editorViewModel?.let { it.isInitializing || it.isBuildInProgress } == true || buildService?.isBuildInProgress == true
  }
}

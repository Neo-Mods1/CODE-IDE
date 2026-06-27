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



package com.neo.ide.handlers

import com.neo.ide.R
import com.neo.ide.activities.editor.EditorHandlerActivity
import com.neo.ide.preferences.internal.GeneralPreferences
import com.neo.ide.resources.R.string
import com.neo.ide.services.builder.GradleBuildService
import com.neo.ide.tooling.api.messages.result.BuildInfo
import com.neo.ide.tooling.events.ProgressEvent
import com.neo.ide.tooling.events.configuration.ProjectConfigurationStartEvent
import com.neo.ide.tooling.events.task.TaskStartEvent
import com.neo.ide.utils.flashError
import com.neo.ide.utils.flashSuccess
import org.slf4j.LoggerFactory
import java.lang.ref.WeakReference

/**
 * Handles events received from [GradleBuildService] updates [EditorHandlerActivity].
 * @author Akash Yadav
 */
class EditorBuildEventListener : GradleBuildService.EventListener {

  private var enabled = true
  private var activityReference: WeakReference<EditorHandlerActivity> = WeakReference(null)

  companion object {

    private val log = LoggerFactory.getLogger(EditorBuildEventListener::class.java)
  }

  private val _activity: EditorHandlerActivity?
    get() = activityReference.get()
  private val activity: EditorHandlerActivity
    get() = checkNotNull(activityReference.get()) { "Activity reference has been destroyed!" }

  fun setActivity(activity: EditorHandlerActivity) {
    this.activityReference = WeakReference(activity)
    this.enabled = true
  }

  fun release() {
    activityReference.clear()
    this.enabled = false
  }

  override fun prepareBuild(buildInfo: BuildInfo) {
    checkActivity("prepareBuild") ?: return

    val isFirstBuild = GeneralPreferences.isFirstBuild
    activity
      .setStatus(
        activity.getString(if (isFirstBuild) string.preparing_first else string.preparing)
      )

    if (isFirstBuild) {
      activity.showFirstBuildNotice()
    }

    activity.editorViewModel.isBuildInProgress = true
    activity.content.bottomSheet.clearBuildOutput()

    if (buildInfo.tasks.isNotEmpty()) {
      activity.content.bottomSheet.appendBuildOut(
        activity.getString(R.string.title_run_tasks) + " : " + buildInfo.tasks)
    }
  }

  override fun onBuildSuccessful(tasks: List<String?>) {
    checkActivity("onBuildSuccessful") ?: return

    analyzeCurrentFile()

    GeneralPreferences.isFirstBuild = false
    activity.editorViewModel.isBuildInProgress = false

    activity.flashSuccess(R.string.build_status_sucess)
  }

  override fun onProgressEvent(event: ProgressEvent) {
    checkActivity("onProgressEvent") ?: return

    if (event is ProjectConfigurationStartEvent || event is TaskStartEvent) {
      activity.setStatus(event.descriptor.displayName)
    }
  }

  override fun onBuildFailed(tasks: List<String?>) {
    checkActivity("onBuildFailed") ?: return

    analyzeCurrentFile()

    GeneralPreferences.isFirstBuild = false
    activity.editorViewModel.isBuildInProgress = false

    activity.flashError(R.string.build_status_failed)
  }

  override fun onOutput(line: String?) {
    checkActivity("onOutput") ?: return

    line?.let { activity.appendBuildOutput(it) }
    // TODO This can be handled better when ProgressEvents are received from Tooling API server
    if (line!!.contains("BUILD SUCCESSFUL") || line.contains("BUILD FAILED")) {
      activity.setStatus(line)
    }
  }

  private fun analyzeCurrentFile() {
    checkActivity("analyzeCurrentFile") ?: return

    val editorView = _activity?.getCurrentEditor()
    if (editorView != null) {
      val editor = editorView.editor
      editor?.analyze()
    }
  }

  private fun checkActivity(action: String): EditorHandlerActivity? {
    if (!enabled) return null

    return _activity.also {
      if (it == null) {
        log.warn("[{}] Activity reference has been destroyed!", action)
        enabled = false
      }
    }
  }
}

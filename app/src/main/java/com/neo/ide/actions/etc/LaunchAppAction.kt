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



package com.neo.ide.actions.etc

import android.content.Context
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.neo.ide.R
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.EditorActivityAction
import com.neo.ide.actions.markInvisible
import com.neo.ide.actions.openApplicationModuleChooser
import com.neo.ide.projects.IProjectManager
import com.neo.ide.projects.android.androidAppProjects
import com.neo.ide.utils.IntentUtils
import com.neo.ide.utils.flashError
import org.slf4j.LoggerFactory

/**
 * An action to launch the already installed application on the device.
 *
 * @author Akash Yadav
 */
class LaunchAppAction(context: Context, override val order: Int) : EditorActivityAction() {

  override val id: String = "ide.editor.launchInstalledApp"
  override var requiresUIThread: Boolean = true

  init {
    label = context.getString(R.string.title_launch_app)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_open_external)
  }

  companion object {
    private val log = LoggerFactory.getLogger(LaunchAppAction::class.java)
  }

  override fun prepare(data: ActionData) {
    super.prepare(data)
    data.getActivity() ?: run {
      markInvisible()
      return
    }

    visible = true

    enabled = IProjectManager.getInstance()
      .getWorkspace()
      ?.androidAppProjects()
      ?.iterator()
      ?.hasNext() == true
  }

  override suspend fun execAction(data: ActionData) {
    openApplicationModuleChooser(data) { app ->
      val variant = app.getSelectedVariant()

      log.debug("Selected variant: {}", variant?.name)

      if (variant == null) {
        flashError(R.string.err_selected_variant_not_found)
        return@openApplicationModuleChooser
      }

      val applicationId = variant.mainArtifact.applicationId
      if (applicationId == null) {
        log.error("Unable to launch application. variant.mainArtifact.applicationId is null")
        flashError(R.string.err_cannot_determine_package)
        return@openApplicationModuleChooser
      }

      log.info("Launching application: {}", applicationId)

      val activity = data.requireActivity()
      IntentUtils.launchApp(activity, applicationId, logError = false)
    }
  }

  override fun getShowAsActionFlags(data: ActionData): Int {
    // prefer showing this in the overflow menu
    return MenuItem.SHOW_AS_ACTION_IF_ROOM
  }
}
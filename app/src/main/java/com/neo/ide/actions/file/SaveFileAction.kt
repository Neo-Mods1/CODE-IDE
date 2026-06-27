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



package com.neo.ide.actions.file

import android.content.Context
import androidx.core.content.ContextCompat
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.EditorRelatedAction
import com.neo.ide.models.SaveResult
import com.neo.ide.projects.internal.ProjectManagerImpl
import com.neo.ide.resources.R
import com.neo.ide.utils.flashError
import com.neo.ide.utils.flashSuccess
import org.slf4j.LoggerFactory

/** @author Akash Yadav */
class SaveFileAction(context: Context, override val order: Int) : EditorRelatedAction() {

  override val id: String = "ide.editor.files.saveAll"
  override var requiresUIThread: Boolean = false

  companion object {
    private val log = LoggerFactory.getLogger(SaveFileAction::class.java)
  }

  init {
    label = context.getString(R.string.save)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_save)
  }

  override fun prepare(data: ActionData) {
    super.prepare(data)
    val context = data.getActivity() ?: run {
      visible = false
      enabled = false
      return
    }

    visible = context.editorViewModel.getOpenedFiles().isNotEmpty()
    enabled = context.areFilesModified() && !context.areFilesSaving()
  }

  override suspend fun execAction(data: ActionData): ResultWrapper {
    val context = data.getActivity() ?: return ResultWrapper()

    if (context.areFilesSaving()) {
      return ResultWrapper(isAlreadySaving = true)
    }

    return try {
      // Cannot use context.saveAll() because this.execAction is called on non-UI thread
      // and saveAll call will result in UI actions
      ResultWrapper(result = context.saveAllResult())
    } catch (error: Throwable) {
      log.error("Failed to save file", error)
      ResultWrapper()
    }
  }

  override fun postExec(data: ActionData, result: Any) {
    if (result is ResultWrapper && result.result != null) {
      val context = data.requireActivity()

      if (result.isAlreadySaving) {
        context.flashError(R.string.msg_files_being_saved)
        return
      }

      // show save notification before calling 'notifySyncNeeded' so that the file save notification
      // does not overlap the sync notification
      context.flashSuccess(R.string.all_saved)

      val saveResult = result.result
      if (saveResult.xmlSaved) {
        ProjectManagerImpl.getInstance().generateSources()
      }

      if (saveResult.gradleSaved) {
        context.editorViewModel.isSyncNeeded = true
      }

      context.invalidateOptionsMenu()
    } else {
      log.error("Failed to save file")
      flashError(R.string.save_failed)
    }
  }

  inner class ResultWrapper(val isAlreadySaving: Boolean = false, val result: SaveResult? = null)
}

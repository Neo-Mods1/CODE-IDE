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



package com.neo.ide.actions.filetree

import android.app.ProgressDialog
import android.content.Context
import com.blankj.utilcode.util.FileUtils
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.requireFile
import com.neo.ide.eventbus.events.file.FileDeletionEvent
import com.neo.ide.projects.FileManager
import com.neo.ide.resources.R
import com.neo.ide.tasks.executeAsync
import com.neo.ide.utils.DialogUtils
import com.neo.ide.utils.FlashType
import com.neo.ide.utils.flashMessage
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * File tree action to delete files.
 *
 * @author Akash Yadav
 */
class DeleteAction(context: Context, override val order: Int) :
  BaseFileTreeAction(context, labelRes = R.string.delete_file, iconRes = R.drawable.ic_delete) {

  override val id: String = "ide.editor.fileTree.delete"

  override suspend fun execAction(data: ActionData) {
    val context = data.requireActivity()
    val file = data.requireFile()
    val lastHeld = data.getTreeNode()
    val builder = DialogUtils.newMaterialDialogBuilder(context)
    builder
      .setNegativeButton(R.string.no, null)
      .setPositiveButton(R.string.yes) { dialogInterface, _ ->
        dialogInterface.dismiss()
        @Suppress("DEPRECATION")
        val progressDialog =
          ProgressDialog.show(context, null, context.getString(R.string.please_wait), true, false)
        executeAsync({ FileUtils.delete(file) }) {
          progressDialog.dismiss()

          val deleted = it ?: false

          flashMessage(
            if (deleted) R.string.deleted else R.string.delete_failed,
            if (deleted) FlashType.SUCCESS else FlashType.ERROR
          )

          if (!deleted) {
            return@executeAsync
          }

          notifyFileDeleted(file, context)

          if (lastHeld != null) {
            val parent = lastHeld.parent
            parent.deleteChild(lastHeld)
            requestExpandNode(parent)
          } else {
            requestFileListing()
          }

          val frag = context.getEditorForFile(file)
          if (frag != null) {
            context.closeFile(context.findIndexOfEditorByFile(frag.file))
          }
        }
      }
      .setTitle(R.string.title_confirm_delete)
      .setMessage(
        context.getString(
          R.string.msg_confirm_delete,
          String.format("%s [%s]", file.name, file.absolutePath)
        )
      )
      .setCancelable(false)
      .create()
      .show()
  }

  private fun notifyFileDeleted(file: File, context: Context) {
    val deletionEvent = FileDeletionEvent(file)

    // Notify FileManager first
    FileManager.onFileDeleted(deletionEvent)

    EventBus.getDefault().post(deletionEvent.putData(context))
  }
}

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

import android.content.Context
import android.view.LayoutInflater
import com.blankj.utilcode.util.FileUtils
import com.neo.ide.R
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.requireFile
import com.neo.ide.adapters.viewholders.FileTreeViewHolder
import com.neo.ide.eventbus.events.file.FileRenameEvent
import com.neo.ide.preferences.databinding.LayoutDialogTextInputBinding
import com.neo.ide.projects.FileManager
import com.neo.ide.tasks.launchAsyncWithProgress
import com.neo.ide.utils.DialogUtils
import com.neo.ide.utils.FlashType
import com.neo.ide.utils.flashMessage
import com.unnamed.b.atv.model.TreeNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * Action to rename the selected file.
 *
 * @author Akash Yadav
 */
class RenameAction(context: Context, override val order: Int) :
  BaseFileTreeAction(
    context,
    labelRes = R.string.rename_file,
    iconRes = R.drawable.ic_file_rename
  ) {

  override val id: String = "ide.editor.fileTree.rename"

  override suspend fun execAction(data: ActionData) {
    val context = data.requireActivity()
    val file = data.requireFile()
    val lastHeld = data.getTreeNode()
    val binding = LayoutDialogTextInputBinding.inflate(LayoutInflater.from(context))
    val builder = DialogUtils.newMaterialDialogBuilder(context)
    binding.name.editText!!.hint =
      context.getString(com.neo.ide.resources.R.string.new_name)
    binding.name.editText!!.setText(file.name)
    builder.setTitle(com.neo.ide.resources.R.string.rename_file)
    builder.setMessage(com.neo.ide.resources.R.string.msg_rename_file)
    builder.setView(binding.root)
    builder.setNegativeButton(android.R.string.cancel, null)
    builder.setPositiveButton(com.neo.ide.resources.R.string.rename_file) {
      dialogInterface,
      _ ->
      dialogInterface.dismiss()
      actionScope.launchAsyncWithProgress(
          configureFlashbar = { builder, cancelChecker ->
            builder.message(com.neo.ide.resources.R.string.please_wait)
          },
          action = { _, _ ->
            val name: String = binding.name.editText!!.text.toString().trim()
            val renamed = name.length in 1..40 && FileUtils.rename(file, name)

            if (renamed) {
              notifyFileRenamed(file, name, context)
            }

            withContext(Dispatchers.Main) {
              flashMessage(
                  if (renamed) com.neo.ide.resources.R.string.renamed
                  else com.neo.ide.resources.R.string.rename_failed,
                  if (renamed) FlashType.SUCCESS else FlashType.ERROR)
              if (!renamed) {
                return@withContext
              }

              if (lastHeld != null) {
                val parent = lastHeld.parent
                parent.deleteChild(lastHeld)
                val node = TreeNode(File(file.parentFile, name))
                node.viewHolder = FileTreeViewHolder(context)
                parent.addChild(node)
                requestExpandNode(parent)
              } else {
                requestFileListing()
              }
            }
          })
    }
    builder.create().show()
  }

  private fun notifyFileRenamed(file: File, name: String, context: Context) {
    val renameEvent = FileRenameEvent(file, File(file.parent, name))

    // Notify FileManager first
    FileManager.onFileRenamed(renameEvent)

    EventBus.getDefault().post(renameEvent.apply { putData(context) })
  }
}

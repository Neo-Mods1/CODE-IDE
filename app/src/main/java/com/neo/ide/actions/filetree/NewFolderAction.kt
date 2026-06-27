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
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.requireFile
import com.neo.ide.adapters.viewholders.FileTreeViewHolder
import com.neo.ide.preferences.databinding.LayoutDialogTextInputBinding
import com.neo.ide.resources.R
import com.neo.ide.utils.DialogUtils
import com.neo.ide.utils.flashError
import com.neo.ide.utils.flashSuccess
import com.unnamed.b.atv.model.TreeNode
import java.io.File

/**
 * File tree action to create a new folder.
 *
 * @author Akash Yadav
 */
class NewFolderAction(context: Context, override val order: Int) :
  BaseDirNodeAction(
    context = context,
    labelRes = R.string.new_folder,
    iconRes = R.drawable.ic_new_folder
  ) {

  override val id: String = "ide.editor.fileTree.newFolder"

  override suspend fun execAction(data: ActionData) {
    val context = data.requireActivity()
    val currentDir = data.requireFile()
    val lastHeld = data.getTreeNode()
    val binding = LayoutDialogTextInputBinding.inflate(LayoutInflater.from(context))
    val builder = DialogUtils.newMaterialDialogBuilder(context)
    binding.name.editText!!.setHint(R.string.folder_name)
    builder.setTitle(R.string.new_folder)
    builder.setMessage(R.string.msg_can_contain_slashes)
    builder.setView(binding.root)
    builder.setCancelable(false)
    builder.setPositiveButton(R.string.text_create) { dialogInterface, _ ->
      dialogInterface.dismiss()
      val name: String = binding.name.editText!!.text.toString().trim()
      if (name.length !in 1..40 || name.startsWith("/")) {
        flashError(R.string.msg_invalid_name)
        return@setPositiveButton
      }

      val newDir = File(currentDir, name)
      if (newDir.exists()) {
        flashError(R.string.msg_folder_exists)
        return@setPositiveButton
      }

      if (!newDir.mkdirs()) {
        flashError(R.string.msg_folder_creation_failed)
        return@setPositiveButton
      }

      flashSuccess(R.string.msg_folder_created)
      if (lastHeld != null) {
        val node = TreeNode(newDir)
        node.viewHolder = FileTreeViewHolder(context)
        lastHeld.addChild(node)
        requestExpandNode(lastHeld)
      } else {
        requestFileListing()
      }
    }
    builder.setNegativeButton(android.R.string.cancel, null)
    builder.create().show()
  }
}

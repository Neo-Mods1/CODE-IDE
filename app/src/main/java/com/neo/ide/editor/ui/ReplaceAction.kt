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



package com.neo.ide.editor.ui

import android.view.LayoutInflater
import com.neo.ide.editor.databinding.LayoutEditorFindReplaceBinding
import com.neo.ide.resources.R
import com.neo.ide.utils.DialogUtils
import org.slf4j.LoggerFactory

/**
 * Handles the replace action while searching in file.
 *
 * @author Akash Yadav
 */
object ReplaceAction {

  private val log = LoggerFactory.getLogger(ReplaceAction::class.java)

  @JvmStatic
  fun doReplace(editor: IDEEditor) {
    val context = editor.context
    val binding = LayoutEditorFindReplaceBinding.inflate(LayoutInflater.from(context))
    val builder = DialogUtils.newMaterialDialogBuilder(context)
    builder.setTitle(R.string.replace)
    builder.setView(binding.root)
    builder.setNegativeButton(android.R.string.cancel, null)
    builder.setPositiveButton(R.string.replace) { dialog, _ ->
      dialog.dismiss()
      val input = binding.replacementInput.editText
      if (input == null) {
        log.error("Unable to perform replace action. Input field is null")
        return@setPositiveButton
      }

      editor.searcher.replaceThis(input.text.toString())
    }
    builder.setNeutralButton(R.string.replaceAll) { dialog, _ ->
      dialog.dismiss()
      val input = binding.replacementInput.editText
      if (input == null) {
        log.error("Unable to perform replace action. Input field is null")
        return@setNeutralButton
      }

      editor.searcher.replaceAll(input.text.toString())
    }
    builder.show()
  }
}

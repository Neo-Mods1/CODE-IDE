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

package com.neo.ide.lsp.java.actions.common

import com.google.googlejavaformat.java.FormatterException
import com.google.googlejavaformat.java.RemoveUnusedImports
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.hasRequiredData
import com.neo.ide.actions.markInvisible
import com.neo.ide.actions.requireEditor
import com.neo.ide.lsp.java.actions.BaseJavaCodeAction
import com.neo.ide.resources.R.string
import io.github.rosemoe.sora.widget.CodeEditor
import org.slf4j.LoggerFactory

class RemoveUnusedImportsAction : BaseJavaCodeAction() {

  override val id: String = "ide.editor.lsp.java.removeUnusedImports"
  override var label: String = ""
  override val titleTextRes: Int = string.action_remove_unused_imports

  companion object {

    private val log = LoggerFactory.getLogger(RemoveUnusedImportsAction::class.java)
  }

  override fun prepare(data: ActionData) {
    super.prepare(data)
    if (!visible) {
      return
    }

    if (!data.hasRequiredData(CodeEditor::class.java)) {
      markInvisible()
      return
    }

    visible = true
    enabled = true
  }

  override suspend fun execAction(data: ActionData): Any {
    val watch = com.neo.ide.utils.StopWatch("Remove unused imports")
    return try {
      val editor = data.requireEditor()
      val content = editor.text
      val output = RemoveUnusedImports.removeUnusedImports(content.toString())
      watch.log()
      output
    } catch (e: FormatterException) {
      log.error("Failed to remove unused imports", e)
      false
    }
  }

  override fun postExec(data: ActionData, result: Any) {
    if (result is String && result.isNotEmpty()) {
      val editor = data.requireEditor()
      editor.setText(result)
    }
  }
}

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
import com.google.googlejavaformat.java.ImportOrderer
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.hasRequiredData
import com.neo.ide.actions.markInvisible
import com.neo.ide.actions.requireEditor
import com.neo.ide.editor.api.IEditor
import com.neo.ide.lsp.java.JavaLanguageServer
import com.neo.ide.lsp.java.actions.BaseJavaCodeAction
import com.neo.ide.lsp.java.models.JavaServerSettings
import com.neo.ide.resources.R.string
import io.github.rosemoe.sora.widget.CodeEditor
import org.slf4j.LoggerFactory

class OrganizeImportsAction : BaseJavaCodeAction() {

  override val id: String = "lsp_java_organizeImports"
  override var label: String = ""
  override val titleTextRes: Int = string.action_organize_imports

  companion object {

    private val log = LoggerFactory.getLogger(OrganizeImportsAction::class.java)
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
    val watch = com.neo.ide.utils.StopWatch("Organize imports")
    return try {
      val editor = data.requireEditor()
      val content = editor.text
      val server = data[JavaLanguageServer::class.java]
      val settings = server!!.settings as JavaServerSettings
      val output = ImportOrderer.reorderImports(content.toString(), settings.style)
      watch.log()
      output
    } catch (e: FormatterException) {
      log.error("Failed to reorder imports", e)
      false
    }
  }

  override fun postExec(data: ActionData, result: Any) {
    super.postExec(data, result)
    if (result is String) {
      if (result.isNotEmpty()) {
        val editor = data.requireEditor()
        val cursor = editor.cursor.left()

        editor.text.apply {
          val endLine = getLine(lineCount - 1)
          replace(0, 0, lineCount - 1, endLine.length + endLine.lineSeparator.length, result)
        }

        (editor as? IEditor?)?.also {
          it.setSelectionAround(cursor)
          editor.ensureSelectionVisible()
        }
      }
    }
  }
}

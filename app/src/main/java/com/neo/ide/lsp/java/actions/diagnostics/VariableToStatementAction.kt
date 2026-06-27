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


package com.neo.ide.lsp.java.actions.diagnostics

import com.neo.ide.actions.ActionData
import com.neo.ide.actions.hasRequiredData
import com.neo.ide.actions.markInvisible
import com.neo.ide.actions.requireFile
import com.neo.ide.actions.requirePath
import com.neo.ide.lsp.java.JavaCompilerProvider
import com.neo.ide.lsp.java.actions.BaseJavaCodeAction
import com.neo.ide.lsp.java.models.DiagnosticCode
import com.neo.ide.lsp.java.rewrite.ConvertVariableToStatement
import com.neo.ide.lsp.java.utils.CodeActionUtils.findPosition
import com.neo.ide.projects.IProjectManager
import com.neo.ide.resources.R
import org.slf4j.LoggerFactory

/** @author Akash Yadav */
class VariableToStatementAction : BaseJavaCodeAction() {

  override val id: String = "ide.editor.lsp.java.diagnostics.variableToStatement"
  override var label: String = ""
  private val diagnosticCode = DiagnosticCode.UNUSED_LOCAL.id

  override val titleTextRes: Int = R.string.action_convert_to_statement

  companion object {

    private val log = LoggerFactory.getLogger(VariableToStatementAction::class.java)
  }

  override fun prepare(data: ActionData) {
    super.prepare(data)

    if (!visible) {
      return
    }

    if (!data.hasRequiredData(com.neo.ide.lsp.models.DiagnosticItem::class.java)) {
      markInvisible()
      return
    }

    val diagnostic = data.get(com.neo.ide.lsp.models.DiagnosticItem::class.java)!!
    if (diagnosticCode != diagnostic.code) {
      markInvisible()
      return
    }

    visible = true
    enabled = true
  }

  override suspend fun execAction(data: ActionData): Any {
    val diagnostic = data[com.neo.ide.lsp.models.DiagnosticItem::class.java]!!
    val compiler =
      JavaCompilerProvider.get(
        IProjectManager.getInstance().getWorkspace()?.findModuleForFile(data.requireFile(), false)
          ?: return Any()
      )
    val path = data.requirePath()

    return compiler.compile(path).get {
      ConvertVariableToStatement(path, findPosition(it, diagnostic.range.start))
    }
  }

  override fun postExec(data: ActionData, result: Any) {
    if (result !is ConvertVariableToStatement) {
      log.warn("Unable to convert variable to statement")
      return
    }

    performCodeAction(data, result)
  }
}

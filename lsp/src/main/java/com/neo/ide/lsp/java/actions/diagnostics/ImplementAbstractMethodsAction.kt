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
import com.neo.ide.javac.services.util.JavaDiagnosticUtils
import com.neo.ide.lsp.java.actions.BaseJavaCodeAction
import com.neo.ide.lsp.java.models.DiagnosticCode
import com.neo.ide.lsp.java.rewrite.ImplementAbstractMethods
import com.neo.ide.resources.R
import jdkx.tools.Diagnostic
import jdkx.tools.JavaFileObject
import org.slf4j.LoggerFactory

/** @author Akash Yadav */
class ImplementAbstractMethodsAction : BaseJavaCodeAction() {

  override val id: String = "ide.editor.lsp.java.diagnostics.implementAbstractMethods"
  override var label: String = ""
  private var diagnosticCode = DiagnosticCode.DOES_NOT_OVERRIDE_ABSTRACT.id

  override val titleTextRes: Int = R.string.action_implement_abstract_methods

  companion object {

    private val log = LoggerFactory.getLogger(ImplementAbstractMethodsAction::class.java)
  }

  @Suppress("UNCHECKED_CAST")
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
    if (diagnosticCode != diagnostic.code || diagnostic.extra !is Diagnostic<*>) {
      markInvisible()
      return
    }

    JavaDiagnosticUtils.asJCDiagnostic(diagnostic.extra as Diagnostic<out JavaFileObject>)
      ?: run {
        markInvisible()
        return
      }

    visible = true
    enabled = true
  }

  @Suppress("UNCHECKED_CAST")
  override suspend fun execAction(data: ActionData): Any {
    val diagnostic =
      JavaDiagnosticUtils.asJCDiagnostic(
        data.get(
          com.neo.ide.lsp.models.DiagnosticItem::class.java)!!.extra as Diagnostic<out JavaFileObject>
      )
    return ImplementAbstractMethods(diagnostic!!)
  }

  override fun postExec(data: ActionData, result: Any) {
    if (result !is ImplementAbstractMethods) {
      log.warn("Unable to perform action. Invalid result from execAction(..)")
      return
    }

    performCodeAction(data, result)
  }
}

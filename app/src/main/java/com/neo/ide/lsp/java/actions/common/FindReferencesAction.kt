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

import com.neo.ide.actions.ActionData
import com.neo.ide.actions.hasRequiredData
import com.neo.ide.actions.markInvisible
import com.neo.ide.editor.api.ILspEditor
import com.neo.ide.lsp.java.actions.BaseJavaCodeAction
import com.neo.ide.resources.R
import io.github.rosemoe.sora.widget.CodeEditor
import java.io.File

/**
 * Action that allows the user to find references to a variable, field, method or class.
 *
 * @author Akash Yadav
 */
class FindReferencesAction : BaseJavaCodeAction() {

  override val titleTextRes: Int = R.string.action_find_references
  override val id: String = "ide.editor.lsp.java.findReferences"
  override var label: String = ""
  override var requiresUIThread: Boolean = true

  override fun prepare(data: ActionData) {
    super.prepare(data)

    if (!visible || !data.hasRequiredData(CodeEditor::class.java, File::class.java)) {
      markInvisible()
      return
    }
  }

  override suspend fun execAction(data: ActionData): Any {
    val editor = data[CodeEditor::class.java]!!
    return (editor as? ILspEditor)?.findReferences() ?: false
  }
}

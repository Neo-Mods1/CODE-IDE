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
import com.neo.ide.actions.requireEditor
import com.neo.ide.lsp.java.actions.BaseJavaCodeAction
import com.neo.ide.resources.R

/** @author Akash Yadav */
class UncommentAction : BaseJavaCodeAction() {
  override val id: String = "ide.editor.lsp.java.uncommentLine"
  override var label: String = ""

  override val titleTextRes: Int = R.string.action_uncomment_line
  
  override var requiresUIThread: Boolean = true
  
  override suspend fun execAction(data: ActionData): Boolean {
    val editor = data.requireEditor()
    val text = editor.text
    val cursor = editor.cursor
    
    text.beginBatchEdit()
    for (line in cursor.leftLine..cursor.rightLine) {
      val l = text.getLineString(line)
      if (l.trim().startsWith("//")) {
        val i = l.indexOf("//")
        text.delete(line, i, line, i + 2)
      }
    }
    text.endBatchEdit()

    return true
  }
  
  override fun dismissOnAction() = false
}

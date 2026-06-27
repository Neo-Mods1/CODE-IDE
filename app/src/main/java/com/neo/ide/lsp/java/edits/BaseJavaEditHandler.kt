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



package com.neo.ide.lsp.java.edits

import com.neo.ide.editor.api.ILspEditor
import com.neo.ide.lsp.edits.DefaultEditHandler
import com.neo.ide.lsp.models.Command
import io.github.rosemoe.sora.widget.CodeEditor

/**
 * Implementation of [DefaultEditHandler] which avoids reflection in
 * [DefaultEditHandler.executeCommand].
 *
 * @author Akash Yadav
 */
open class BaseJavaEditHandler : DefaultEditHandler() {

  override fun executeCommand(editor: CodeEditor, command: Command?) {
    if (editor is ILspEditor) {
      editor.executeCommand(command)
      return
    }
    super.executeCommand(editor, command)
  }
}

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



package com.neo.ide.lsp.util

import androidx.annotation.UiThread
import com.neo.ide.lsp.models.TextEdit
import io.github.rosemoe.sora.widget.CodeEditor

/** @author Akash Yadav */
class RewriteHelper {
  companion object {
    @UiThread
    @JvmStatic
    fun performEdits(edits: List<TextEdit>, editor: CodeEditor) {
      if (edits.isEmpty()) {
        return
      }

      edits.forEach {
        val s = it.range.start
        val e = it.range.end
        if (s == e) {
          editor.text.insert(s.line, s.column, it.newText)
        } else {
          editor.text.replace(s.line, s.column, e.line, e.column, it.newText)
        }
      }
    }
  }
}

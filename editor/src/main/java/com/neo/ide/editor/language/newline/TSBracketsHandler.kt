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



package com.neo.ide.editor.language.newline

import com.neo.ide.editor.language.treesitter.TreeSitterLanguage
import io.github.rosemoe.sora.lang.smartEnter.NewlineHandleResult
import io.github.rosemoe.sora.lang.styling.Styles
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.TextUtils

/**
 * Newline handler for tree-sitter languages.
 *
 * @author Akash Yadav
 */
abstract class TSBracketsHandler(private val language: TreeSitterLanguage) : BaseNewlineHandler() {

  override fun handleNewline(
    text: Content,
    position: CharPosition,
    style: Styles?,
    tabSize: Int
  ): NewlineHandleResult {
    val count = TextUtils.countLeadingSpaceCount(text.getLine(position.line), tabSize)
    var txt: String
    val sb =
      StringBuilder("\n")
        .append(TextUtils.createIndent(count + tabSize, tabSize, language.useTab()))
        .append("\n")
        .append(
          TextUtils.createIndent(count, tabSize, language.useTab()).also { txt = it }
        )
    return NewlineHandleResult(sb, txt.length + 1)
  }
}

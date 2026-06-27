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

import io.github.rosemoe.sora.lang.smartEnter.NewlineHandleResult
import io.github.rosemoe.sora.lang.styling.Styles
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.TextUtils

internal open class BracketsNewlineHandler(
  val getIndentAdvance: (String?) -> Int,
  val useTab: () -> Boolean
) : CStyleBracketsHandler() {

  override fun handleNewline(
    text: Content,
    position: CharPosition,
    style: Styles?,
    tabSize: Int
  ): NewlineHandleResult {
    val line = text.getLine(position.line)
    val index = position.column
    val beforeText = line.subSequence(0, index).toString()
    val afterText = line.subSequence(index, line.length).toString()
    return handleNewline(beforeText, afterText, tabSize)
  }

  private fun handleNewline(
    beforeText: String?,
    afterText: String?,
    tabSize: Int
  ): NewlineHandleResult {
    val count = TextUtils.countLeadingSpaceCount(beforeText!!, tabSize)
    val advanceBefore: Int = getIndentAdvance(beforeText)
    val advanceAfter: Int = getIndentAdvance(afterText)
    var text: String
    val sb =
      StringBuilder("\n")
        .append(TextUtils.createIndent(count + advanceBefore, tabSize, useTab()))
        .append('\n')
        .append(TextUtils.createIndent(count + advanceAfter, tabSize, useTab()).also { text = it })
    val shiftLeft = text.length + 1
    return NewlineHandleResult(sb, shiftLeft)
  }
}

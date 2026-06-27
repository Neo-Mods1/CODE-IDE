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

import io.github.rosemoe.sora.lang.smartEnter.NewlineHandler
import io.github.rosemoe.sora.lang.styling.Styles
import io.github.rosemoe.sora.lang.styling.StylesUtils
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.Content
import kotlin.math.max
import kotlin.math.min

/**
 * Base class for new line handler implementations.
 *
 * @author Akash Yadav
 */
abstract class BaseNewlineHandler : NewlineHandler {
  
  protected val openingBrackets = mutableListOf<String>()
  protected val closingBrackets = mutableListOf<String>()
  
  override fun matchesRequirement(text: Content, position: CharPosition, style: Styles?): Boolean {
    val line = text.getLine(position.line)
    return !StylesUtils.checkNoCompletion(style, position) &&
      (getNonEmptyTextBefore(line, position.column, 1) in openingBrackets) &&
      (getNonEmptyTextAfter(line, position.column, 1) in closingBrackets)
  }
  
  @Suppress("SameParameterValue")
  protected open fun getNonEmptyTextBefore(text: CharSequence, index: Int, length: Int): String {
    var idx = index
    while (idx > 0 && Character.isWhitespace(text[idx - 1])) {
      idx--
    }
    return text.subSequence(max(0, idx - length), idx).toString()
  }
  
  @Suppress("SameParameterValue")
  protected open fun getNonEmptyTextAfter(text: CharSequence, index: Int, length: Int): String {
    var idx = index
    while (idx < text.length && Character.isWhitespace(text[idx])) {
      idx++
    }
    return text.subSequence(idx, min(idx + length, text.length)).toString()
  }
}
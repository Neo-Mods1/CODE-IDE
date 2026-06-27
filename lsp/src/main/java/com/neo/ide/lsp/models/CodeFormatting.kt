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



package com.neo.ide.lsp.models

import com.neo.ide.models.Range

/**
 * Parameters containing data required to format source code.
 *
 * @author Akash Yadav
 */
data class FormatCodeParams
@JvmOverloads
constructor(val content: CharSequence, val range: Range = Range.NONE)

/** The code formatting result. */
data class CodeFormatResult
@JvmOverloads
constructor(
  val isIndexed: Boolean = false,
  val edits: MutableList<out TextEdit> = mutableListOf(),
  val indexedTextEdits: MutableList<IndexedTextEdit> = mutableListOf()
) {

  companion object {

    /** Represents no formatting changes. */
    @JvmField val NONE = CodeFormatResult()

    /** Create a [CodeFormatResult] which replaces the whole [content] with the [formatted] text. */
    @JvmStatic
    fun forWholeContent(content: CharSequence, formatted: CharSequence): CodeFormatResult {
      val replacements = mutableListOf<IndexedTextEdit>()
      replacements.add(IndexedTextEdit(0, content.length, formatted))
      return CodeFormatResult(true, indexedTextEdits = replacements)
    }
  }
}

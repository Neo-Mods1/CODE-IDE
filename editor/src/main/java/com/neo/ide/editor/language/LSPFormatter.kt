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



package com.neo.ide.editor.language

import com.neo.ide.lsp.api.ILanguageServer
import com.neo.ide.lsp.models.CodeFormatResult
import com.neo.ide.lsp.models.FormatCodeParams
import com.neo.ide.models.Position
import com.neo.ide.models.Range
import io.github.rosemoe.sora.lang.format.AsyncFormatter
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.TextRange

/**
 * An [AsyncFormatter] implementation which uses the LSP implementation to format code.
 *
 * @author Akash Yadav
 */
class LSPFormatter(val server: ILanguageServer? = null) : AsyncFormatter() {
  
  override fun formatAsync(text: Content, cursorRange: TextRange): TextRange {
    return doFormat(text, cursorRange)
  }

  override fun formatRegionAsync(
    text: Content,
    rangeToFormat: TextRange,
    cursorRange: TextRange
  ): TextRange {
    return doFormat(text, cursorRange, rangeToFormat)
  }

  private fun doFormat(
    text: Content,
    cursorRange: TextRange,
    rangeToFormat: TextRange? = null
  ): TextRange {
    if (server == null) {
      return cursorRange
    }

    val range =
      (rangeToFormat?.asRange() ?: text.wholeRange()).apply {
        start.apply {
          index = (if (line == 0 && column == 0) 0 else text.getCharIndex(line, column))
        }
        end.apply { index = (if (line == 0 && column == 0) 0 else text.getCharIndex(line, column)) }
      }
    val result = server.formatCode(FormatCodeParams(text, range))

    if (!result.hasEdits() ) {
      // Deselect the selected content
      return TextRange(cursorRange.start, cursorRange.start)
    }

    if (result.isIndexed) {
      result.indexedTextEdits.forEach { text.replace(it.start, it.end, it.newText) }
    } else {
      result.edits.forEach {
        text.replace(
          it.range.start.line,
          it.range.start.column,
          it.range.end.line,
          it.range.end.column,
          it.newText
        )
      }
    }
    // Deselect the selected content
    return TextRange(cursorRange.start, cursorRange.start)
  }
}

private fun CodeFormatResult.hasEdits() =
  this.indexedTextEdits.isNotEmpty() || this.edits.isNotEmpty()

private fun TextRange.asRange(): Range {
  return Range().also {
    it.start = this.start.asPosition()
    it.end = this.end.asPosition()
  }
}

private fun Content.wholeRange(): Range {
  return Range(Position(0, 0), Position(lineCount - 1, getColumnCount(lineCount - 1)))
}

private fun CharPosition.asPosition(): Position {
  return Position(this.line, this.column, this.index)
}

private fun Range.asTextRange(): TextRange {
  return TextRange(this.start.asCharPosition(), this.end.asCharPosition())
}

private fun Position.asCharPosition(): CharPosition {
  return CharPosition().also {
    it.line = this.line
    it.column = this.column
    it.index = this.index
  }
}

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



package com.neo.ide.editor.language.treesitter

import android.graphics.Color
import androidx.core.graphics.ColorUtils
import com.neo.ide.editor.schemes.LanguageScheme
import com.neo.ide.treesitter.TSQuery
import com.neo.ide.treesitter.TSQueryCapture
import com.neo.ide.utils.parseHexColor
import io.github.rosemoe.sora.editor.ts.spans.DefaultSpanFactory
import io.github.rosemoe.sora.editor.ts.spans.TsSpanFactory
import io.github.rosemoe.sora.lang.styling.Span
import io.github.rosemoe.sora.lang.styling.SpanFactory
import io.github.rosemoe.sora.lang.styling.Styles
import io.github.rosemoe.sora.lang.styling.span.SpanConstColorResolver
import io.github.rosemoe.sora.lang.styling.span.SpanExtAttrs
import io.github.rosemoe.sora.text.ContentReference
import org.slf4j.LoggerFactory

/**
 * [TsSpanFactory] for tree sitter languages.
 *
 * @author Akash Yadav
 */
class TreeSitterSpanFactory(
  private var content: ContentReference?,
  private var query: TSQuery?,
  private var styles: Styles?,
  private var langScheme: LanguageScheme?
) : DefaultSpanFactory() {

  companion object {

    private val log = LoggerFactory.getLogger(TreeSitterSpanFactory::class.java)

    @JvmStatic
    private val HEX_REGEX = "#\\b([0-9a-fA-F]{3}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})\\b".toRegex()
  }

  override fun close() {
    content = null
    query = null
    styles = null
    langScheme = null
  }

  override fun createSpans(capture: TSQueryCapture, column: Int, spanStyle: Long): List<Span> {
    val content = this.content?.reference ?: return super.createSpans(capture, column, spanStyle)
    val query = this.query ?: return super.createSpans(capture, column, spanStyle)
    val langScheme = this.langScheme ?: return super.createSpans(capture, column, spanStyle)

    val captureName = query.getCaptureNameForId(capture.index)
    val styleDef = langScheme.getStyles()[captureName]
    if (styleDef?.maybeHexColor != true) {
      return super.createSpans(capture, column, spanStyle)
    }

    val (start, end) = content.indexer.run {
      getCharPosition(capture.node.startByte / 2) to getCharPosition(capture.node.endByte / 2)
    }

    if (start.line != end.line || start.column != column) {
      // A HEX color can only be defined on a single line
      return super.createSpans(capture, column, spanStyle)
    }

    val text = content.subContent(start.line, start.column, end.line, end.column)
    val results = HEX_REGEX.findAll(text)
    val spans = mutableListOf<Span>()
    var s = -1
    var e = -1
    results.forEach { result ->
      if (e != -1 && e < result.range.first) {
        // there is some interval between previous color span
        // and this color span
        // fill the gap
        spans.add(Span.obtain(column + e + 1, spanStyle))
      }

      if (s == -1) {
        s = result.range.first
      }
      e = result.range.last

      val color = try {
        parseHexColor(result.groupValues[1]).toInt()
      } catch (e: Exception) {
        log.error("An error occurred parsing hex color. text={}", text, e)
        return@forEach
      }

      val textColor = if (ColorUtils.calculateLuminance(color) > 0.5f) {
        Color.BLACK
      } else {
        Color.WHITE
      }

      val col = column + result.range.first
      val span = SpanFactory.obtain(
        col,
        styleDef.makeStaticStyle()
      )

      span.setSpanExt(SpanExtAttrs.EXT_COLOR_RESOLVER, SpanConstColorResolver(textColor, color))

      spans.add(span)
    }

    if (spans.isEmpty()) {
      return super.createSpans(capture, column, spanStyle)
    }

    // make sure that the default style is used for unmatched regions
    if (s != 0) {
      spans.add(0, SpanFactory.obtain(column, spanStyle))
    }

    if (e != text.lastIndex) {
      spans.add(SpanFactory.obtain(column + e + 1, spanStyle))
    }

    return spans
  }
}
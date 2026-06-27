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



package com.neo.ide.lsp.xml.models

import com.neo.ide.lsp.xml.providers.format.FormatElementCategory
import com.neo.ide.lsp.xml.providers.format.FormatElementCategory.PreserveSpace
import com.neo.ide.preferences.internal.XmlPreferences
import org.eclipse.lemminx.dom.DOMElement
import org.eclipse.lemminx.dom.builder.BaseXmlFormattingOptions
import org.eclipse.lemminx.dom.builder.EmptyElements

/**
 * Options for XML code formatting.
 *
 * @author Akash Yadav
 */
open class XMLFormattingOptions : BaseXmlFormattingOptions() {

  override val isTrimFinalNewLine: Boolean
    get() = XmlPreferences.trimFinalNewLine
  override val isInsertFinalNewLine: Boolean
    get() = XmlPreferences.insertFinalNewLine
  override val isSplitAttributes: Boolean
    get() = XmlPreferences.splitAttributes
  override val isJoinCDataLines: Boolean
    get() = XmlPreferences.joinCDataLines
  override val isJoinCommentLines: Boolean
    get() = XmlPreferences.joinCommentLines
  override val isJoinContentLines: Boolean
    get() = XmlPreferences.joinContentLines
  override val isSpaceBeforeEmptyCloseTag: Boolean
    get() = XmlPreferences.spaceBeforeEmptyCloseTag
  override val isPreserveEmptyContent: Boolean
    get() = XmlPreferences.preserveEmptyContent
  override val isPreserveAttributeLineBreaks: Boolean
    get() = XmlPreferences.preserveAttributeLineBreaks
  override val isClosingBracketNewLine: Boolean
    get() = XmlPreferences.closingBracketNewLine
  override val isTrimTrailingWhitespace: Boolean
    get() = XmlPreferences.trimTrailingWhitespace

  override val maxLineWidth: Int
    get() = XmlPreferences.maxLineWidth
  override val preservedNewLines: Int
    get() = XmlPreferences.preservedNewLines
  override val splitAttributesIndentSize: Int
    get() = XmlPreferences.splitAttributesIndentSize

  override val emptyElementsBehavior: EmptyElements
    get() = EmptyElements.valueOf(
      XmlPreferences.emptyElementsBehavior)

  private val preserveSpace =
    listOf("xsl:text", "xsl:comment", "xsl:processing-instruction",
      "literallayout", "programlisting", "screen", "synopsis", "pre", "xd:pre")

  fun getFormatElementCategory(element: DOMElement): FormatElementCategory? {
    return preserveSpace.find { it == element.tagName }?.let { PreserveSpace }
  }
}

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


package com.neo.ide.lsp.xml.utils

import com.neo.ide.lexers.xml.XMLLexer
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType.ATTRIBUTE
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType.ATTRIBUTE_VALUE
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType.TAG
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType.UNKNOWN
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.Token
import org.eclipse.lemminx.dom.DOMDocument
import org.eclipse.lemminx.dom.DOMElement
import org.eclipse.lemminx.dom.DOMNode
import org.slf4j.LoggerFactory

/** @author Akash Yadav */
object XmlUtils {
  
  private val log = LoggerFactory.getLogger(XmlUtils::class.java)
  
  fun isTag(node: DOMNode, index: Int): Boolean {
    var name = node.nodeName
    if (name.isNullOrBlank()) {
      name = ""
    }
    return node.start < index && index <= node.start + name.length + 1
  }

  fun isEndTag(node: DOMNode?, index: Int): Boolean {
    if (node !is DOMElement) {
      return false
    }
    val endOpenOffset = node.endTagOpenOffset
    return if (endOpenOffset == -1) {
      false
    } else index >= endOpenOffset
  }

  fun isInAttributeValue(contents: String?, index: Int): Boolean {
    val lexer = XMLLexer(CharStreams.fromString(contents))
    var token: Token
    while (lexer.nextToken().also { token = it } != null) {
      val start = token.startIndex
      val end = token.stopIndex
      if (token.type == Token.EOF) {
        break
      }

      if (index in start..end) {
        return token.type == XMLLexer.STRING
      }
      if (end > index) {
        break
      }
    }
    return false
  }

  fun getPrefix(parsed: DOMDocument, index: Int, type: NodeType?): String? {
    val text = parsed.text
    return when (type) {
      TAG -> {
        val nodeAt = parsed.findNodeAt(index) ?: run {
          log.warn("Unable to find node at index {}", index)
          return null
        }
        text.substring(nodeAt.start, index)
      }
      ATTRIBUTE -> {
        val attr = parsed.findAttrAt(index) ?: run {
          log.warn("Unable to find attribute at index {}", index)
          return null
        }
  
        text.substring(attr.start, index)
      }
      ATTRIBUTE_VALUE -> {
        val attrAt = parsed.findAttrAt(index) ?: run {
          log.warn("Unable to find attribute at index {}", index)
          return null
        }
  
        var prefix = text.substring(attrAt.nodeAttrValue.start + 1, index)
        if (prefix.contains("|")) {
          prefix = prefix.substring(prefix.lastIndexOf('|') + 1)
        }
        prefix
      }
      else -> "<this-will-not-match>"
    }
  }

  fun getNodeType(parsed: DOMDocument, cursor: Int): NodeType {
    val nodeAt = parsed.findNodeAt(cursor) ?: run {
      log.warn("Unable to find node at index {}", cursor)
      return UNKNOWN
    }
  
  
    if (isTag(nodeAt, cursor) || isEndTag(nodeAt, cursor)) {
      return TAG
    }

    return if (isInAttributeValue(parsed.textDocument.text, cursor)) {
      ATTRIBUTE_VALUE
    } else {
      ATTRIBUTE
    }
  }
  
  enum class NodeType {
    UNKNOWN,
    TAG,
    ATTRIBUTE,
    ATTRIBUTE_VALUE
  }
}

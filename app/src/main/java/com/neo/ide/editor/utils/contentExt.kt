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



package com.neo.ide.editor.utils

import androidx.core.text.trimmedLength
import com.neo.ide.treesitter.TSNode
import com.neo.ide.treesitter.TSTree
import com.neo.ide.treesitter.getNodeAt
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.text.TextUtils
import io.github.rosemoe.sora.util.IntPair

/**
 * Returns true if the given line is blank.
 */
fun Content.isBlankLine(line: Int) : Boolean {
  return getLine(line).trim { it.isWhitespace() || it == '\r' }.isEmpty()
}

/**
 * Returns true if the given line is not blank.
 */
fun Content.isNonBlankLine(line: Int) : Boolean {
  return !isBlankLine(line)
}

/**
 * Returns the index of the previous non-blank line.
 *
 * @return The index of the previous non-blank line or -1 if not found.
 */
fun Content.previousNonBlankLine(line: Int) : Int {
  for (i in line - 1 downTo 0) {
    if (isNonBlankLine(i)) {
      return i
    }
  }

  return -1
}

/**
 * Returns the index of the next non-blank line.
 *
 * @return The index of the next non-blank line or -1 if not found.
 */
fun Content.nextNonBlankLine(line: Int) : Int {
  for (i in line + 1 until length) {
    if (isNonBlankLine(i)) {
      return i
    }
  }

  return -1
}

/**
 * Returns the first [TSNode] at the given line number. The leading indentation is ignored.
 */
fun Content.getFirstNodeAtLine(tree: TSTree, line: Int, col: Int = Int.MIN_VALUE) : TSNode? {
  return getFirstNodeAtLine(tree.rootNode, line, col)
}

/**
 * Returns the last [TSNode] at the given line number.
 */
fun Content.getLastNodeAtLine(tree: TSTree, line: Int, col: Int = Int.MIN_VALUE) : TSNode? {
  return getLastNodeAtLine(tree.rootNode, line, col)
}

/**
 * Returns the first [TSNode] at the given line number. The leading indentation is ignored.
 */
fun Content.getFirstNodeAtLine(node: TSNode, line: Int, col: Int = Int.MIN_VALUE) : TSNode? {
  if (line < 0 || line >= lineCount) {
    return null
  }

  var column = col
  if (column == Int.MIN_VALUE) {
    val contentLine = getLine(line);
    val (spaces, tabs) = TextUtils.countLeadingSpacesAndTabs(contentLine).let {
      IntPair.getFirst(it) to IntPair.getSecond(it)
    }

    column = (spaces + tabs) shl 1
  }

  // we need the byte offset in the line, so we need to multiply the char offset by 2 (shl 1)
  // also, we need not to expand the tabs to spaces as that would result in incorrect offset
  return node.getNodeAt(line, column)
}

/**
 * Returns the last [TSNode] at the given line number. This function also takes the leading
 * indentation into consideration.
 */
fun Content.getLastNodeAtLine(node: TSNode, line: Int, col: Int = Int.MIN_VALUE) : TSNode? {
  if (line < 0 || line >= lineCount) {
    return null
  }

  val contentLine = getLine(line)

  var column = col
  if (column == Int.MIN_VALUE) {
    column = (contentLine.length - 1) shl 1
  }

  // we need the byte offset in the line, so we need to multiply the char offset by 2 (shl 1)
  return node.getNodeAt(line, column)
}
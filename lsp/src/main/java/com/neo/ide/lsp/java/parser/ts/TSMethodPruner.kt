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



package com.neo.ide.lsp.java.parser.ts

import com.neo.ide.treesitter.TSQuery
import com.neo.ide.treesitter.TSQueryCursor
import com.neo.ide.treesitter.TSQueryMatch
import com.neo.ide.treesitter.TSTree
import com.neo.ide.treesitter.java.TSLanguageJava

/**
 * Helper class to prune method bodies in Java source code using.
 *
 * @author Akash Yadav
 */
object TSMethodPruner {

  private const val METHOD_BODIES_QUERY = "(method_declaration body: (block) @method.body)"

  fun prune(content: StringBuilder, tree: TSTree, cursor: Int) {
    val root = tree.rootNode
    TSQuery.create(TSLanguageJava.getInstance(), METHOD_BODIES_QUERY).use { query ->
      check(query.canAccess()) { "Invalid method bodies query" }
      TSQueryCursor.create().use { queryCursor ->
        queryCursor.exec(query, root)

        var match: TSQueryMatch? = queryCursor.nextMatch()
        while (match != null) {
          val capture = match.captures[0]
          val start = capture.node.startByte / 2
          val end = capture.node.endByte / 2

          if (cursor in start until end) {
            // cursor is located in this method, so do not prune
            match = queryCursor.nextMatch()
            continue
          }
          
          // +1 and -1 to avoid removing the curly braces from the body
          eraseRegion(content, start + 1, end - 1)
          match = queryCursor.nextMatch()
        }
      }
    }
  }

  private fun eraseRegion(content: StringBuilder, start: Int, end: Int) {
    for (i in start until end) {
      if (!content[i].isWhitespace()) {
        content.setCharAt(i, ' ')
      }
    }
  }
}

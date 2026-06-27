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
 * Represents a line-column based text edit. The text in the given [range] must be replaced with the
 * [newText].
 */
data class TextEdit(var range: Range, var newText: String) {
  companion object {
    @JvmField val NONE: TextEdit = TextEdit(Range.NONE, "")
  }
}

/**
 * Represents an index-based text edit. The text from index [start] to [end] must be replaced with
 * [newText].
 */
data class IndexedTextEdit @JvmOverloads constructor(var start: Int = -1, var end: Int = -1, var newText: CharSequence = "") {
  companion object {
    @JvmStatic
    val NONE = IndexedTextEdit()
  }
}

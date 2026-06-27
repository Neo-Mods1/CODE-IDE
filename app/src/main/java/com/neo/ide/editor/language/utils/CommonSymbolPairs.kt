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



package com.neo.ide.editor.language.utils

import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.SymbolPairMatch
import io.github.rosemoe.sora.widget.SymbolPairMatch.SymbolPair.SymbolPairEx

/**
 * Common symbol pairs that can be used in any language.
 *
 * @author Akash Yadav
 */
internal open class CommonSymbolPairs : SymbolPairMatch() {

  private val isSelected =
    object : SymbolPairEx {
      override fun shouldDoAutoSurround(content: Content?): Boolean {
        return content?.cursor?.isSelected ?: false
      }
    }

  init {
    super.putPair('{', SymbolPair("{", "}"))
    super.putPair('(', SymbolPair("(", ")"))
    super.putPair('[', SymbolPair("[", "]"))
    super.putPair('"', SymbolPair("\"", "\"", isSelected))
    super.putPair('\'', SymbolPair("'", "'", isSelected))
  }
}

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



package com.neo.ide.editor.language.incremental

import org.antlr.v4.runtime.Lexer

/**
 * Tokenization state of a line.
 *
 * @param state The state of the line.
 * @param hasBraces `true` if the line has braces. `false` otherwise.
 * @param lexerMode The mode of the lexer. This MUST be preserved in the lexer.
 *
 * @author Akash Yadav
 */
data class LineState(
  @JvmField var state: Int = NORMAL,
  @JvmField var hasBraces: Boolean = false,
  @JvmField var lexerMode: Int = Lexer.DEFAULT_MODE
) {
  companion object {
    const val NORMAL = 0
    const val INCOMPLETE = 1
  }
}

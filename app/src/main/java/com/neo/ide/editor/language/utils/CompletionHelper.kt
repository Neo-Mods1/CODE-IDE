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

import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.ContentReference

/**
 * Utility functions for completion providers.
 *
 * @author Akash Yadav
 */
object CompletionHelper {

  /**
   * Searches backward on the line, with the given checker to check chars.
   * Returns the longest text that matches the requirement.
   *
   * This is a variant of [CompletionHelper.computePrefix][io.github.rosemoe.sora.lang.completion.CompletionHelper.computePrefix]
   * which inlines the predicate for better performance.
   */
  inline fun computePrefix(
    ref: ContentReference, pos: CharPosition,
    checker: (Char) -> Boolean
  ): String {
    var begin = pos.column
    val line = ref.getLine(pos.line)
    while (begin > 0) {
      if (!checker(line[begin - 1])) {
        break
      }
      begin--
    }
    return line.substring(begin, pos.column)
  }
}
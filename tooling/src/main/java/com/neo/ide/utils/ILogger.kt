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

package com.neo.ide.utils

interface ILogger {

  enum class Level(val char: Char) {
    VERBOSE('V'),
    DEBUG('D'),
    INFO('I'),
    WARN('W'),
    ERROR('E');

    companion object {
      @JvmStatic
      fun forChar(c: Char): Level {
        return entries.find { it.char == c } ?: INFO
      }
    }
  }

  companion object {
    const val MSG_SEPARATOR = " "
  }

  fun v(tag: String, msg: String)
  fun v(tag: String, msg: String, tr: Throwable?)
  fun d(tag: String, msg: String)
  fun d(tag: String, msg: String, tr: Throwable?)
  fun i(tag: String, msg: String)
  fun i(tag: String, msg: String, tr: Throwable?)
  fun w(tag: String, msg: String)
  fun w(tag: String, msg: String, tr: Throwable?)
  fun e(tag: String, msg: String)
  fun e(tag: String, msg: String, tr: Throwable?)

  fun generateMessage(vararg args: Any?): String {
    return args.joinToString(separator = MSG_SEPARATOR)
  }
}

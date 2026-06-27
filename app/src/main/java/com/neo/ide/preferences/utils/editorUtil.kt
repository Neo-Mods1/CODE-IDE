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



package com.neo.ide.preferences.utils

import com.neo.ide.preferences.internal.EditorPreferences

/** @author Akash Yadav */

/**
 * The indentation character to use. If [useSoftTab] is enabled, the character is a space, otherwise
 * '\t'.
 */
val indentationChar: Char
  get() = if (EditorPreferences.useSoftTab) ' ' else '\t'

/** Get the string which should be used as indentation while generating code. */
val indentationString: String
  get() = if (EditorPreferences.useSoftTab) " ".repeat(EditorPreferences.tabSize) else "\t"

/**
 * Creates the indentation string for the given number of spaces. The result is simply
 * [indentationChar] repeated [spaceCount] times if [useSoftTab] is enabled, otherwise `spaceCount /
 * tabSize` times.
 *
 * @param spaceCount The number of spaces to indent.
 * @return The indentation string.
 */
fun indentationString(spaceCount: Int): String {
  val count = if (EditorPreferences.useSoftTab) spaceCount else spaceCount / EditorPreferences.tabSize
  return indentationChar.toString().repeat(count)
}

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



package com.neo.ide.lsp.snippets

/**
 * Java snippet.
 *
 * @author Akash Yadav
 */
data class DefaultSnippet(
  override val prefix: String,
  override val description: String,
  override val body: Array<String>
) : ISnippet {

  constructor(
    prefix: String,
    description: String,
    content: () -> Array<String>
  ) : this(prefix, description, content())

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is DefaultSnippet) return false

    if (description != other.description) return false
    if (prefix != other.prefix) return false
    if (!body.contentEquals(other.body)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = prefix.hashCode()
    result = 31 * result + description.hashCode()
    result = 31 * result + body.contentHashCode()
    return result
  }
}

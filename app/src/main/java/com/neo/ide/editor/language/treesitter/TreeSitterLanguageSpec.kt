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



package com.neo.ide.editor.language.treesitter

import com.neo.ide.treesitter.TSLanguage
import com.neo.ide.treesitter.TSQuery
import com.neo.ide.treesitter.TSQueryError
import io.github.rosemoe.sora.editor.ts.TsLanguageSpec
import java.io.Closeable

/**
 * Extension of [TsLanguageSpec] for AndroidIDE.
 *
 * @author Akash Yadav
 */
class TreeSitterLanguageSpec
@JvmOverloads constructor(
  val spec: TsLanguageSpec,
  indentsQueryScm: String = ""
) : Closeable {

  // <editor-fold desc="Proxy properties">
  val language: TSLanguage
    get() = spec.language
  // </editor-fold>

  val indentsQuery: TSQuery? = if (indentsQueryScm.isBlank()) {
    TSQuery.EMPTY
  } else {
    TSQuery.create(language, indentsQueryScm)
      .let { if (it.canAccess()) it else null }
  }

  init {
    indentsQuery?.validateOrThrow(name = "indents")
  }

  override fun close() {
    indentsQuery?.close()
    if (spec.language.isExternal) {
      spec.language.close()
    }
    spec.close()
  }
}

private fun TSQuery.validateOrThrow(name: String) {
  if (errorType != TSQueryError.None) {
    throw IllegalArgumentException(
      "query(name:$name) parsing failed: ${errorType.name} at text offset $errorOffset")
  }
}

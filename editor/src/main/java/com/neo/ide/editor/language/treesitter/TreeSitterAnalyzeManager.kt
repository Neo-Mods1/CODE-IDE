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

import com.neo.ide.editor.schemes.LanguageScheme
import io.github.rosemoe.sora.editor.ts.TsAnalyzeManager
import io.github.rosemoe.sora.editor.ts.TsLanguageSpec
import io.github.rosemoe.sora.editor.ts.TsTheme
import io.github.rosemoe.sora.lang.styling.Styles

/**
 * [TsAnalyzeManager] implementation for tree sitter languages.
 *
 * @author Akash Yadav
 */
class TreeSitterAnalyzeManager(
  languageSpec: TsLanguageSpec,
  theme: TsTheme
) : TsAnalyzeManager(languageSpec, theme) {

  override var styles: Styles = Styles()
    set(value) {
      field = value
      resetSpanFactory(value, langScheme)
    }

  internal var langScheme: LanguageScheme? = null
    set(value) {
      field = value
      resetSpanFactory(styles, value)
    }

  init {
    resetSpanFactory(styles, langScheme)
  }

  private fun resetSpanFactory(styles: Styles, langScheme: LanguageScheme?) {
    spanFactory = TreeSitterSpanFactory(reference, languageSpec.tsQuery, styles, langScheme)
  }
}
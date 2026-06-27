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

import android.content.Context
import com.neo.ide.editor.language.treesitter.TreeSitterLanguage.Factory
import com.neo.ide.lsp.api.ILanguageServer
import com.neo.ide.lsp.api.ILanguageServerRegistry
import com.neo.ide.lsp.xml.XMLLanguageServer
import com.neo.ide.treesitter.xml.TSLanguageXml
import io.github.rosemoe.sora.lang.Language.INTERRUPTION_LEVEL_STRONG
import io.github.rosemoe.sora.util.MyCharacter

/**
 * Tree Sitter language XML language.
 *
 * @author Akash Yadav
 */
class XMLLanguage(context: Context) :
  TreeSitterLanguage(context, lang = TSLanguageXml.getInstance(), langType = TS_TYPE) {

  override val languageServer: ILanguageServer?
    get() = ILanguageServerRegistry.getDefault().getServer(XMLLanguageServer.SERVER_ID)

  companion object {

    const val TS_TYPE = "xml"

    @JvmField
    val FACTORY = Factory { XMLLanguage(it) }
  }

  override fun checkIsCompletionChar(c: Char): Boolean {
    return MyCharacter.isJavaIdentifierPart(c) || c == '<' || c == '/'
  }

  override fun getInterruptionLevel(): Int {
    return INTERRUPTION_LEVEL_STRONG
  }
}

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
import com.neo.ide.editor.language.newline.TSBracketsHandler
import com.neo.ide.editor.language.newline.TSCStyleBracketsHandler
import com.neo.ide.editor.language.treesitter.TreeSitterLanguage.Factory
import com.neo.ide.editor.language.utils.CommonSymbolPairs
import com.neo.ide.lsp.api.ILanguageServer
import com.neo.ide.lsp.api.ILanguageServerRegistry
import com.neo.ide.lsp.java.JavaLanguageServer
import com.neo.ide.treesitter.java.TSLanguageJava
import io.github.rosemoe.sora.lang.Language.INTERRUPTION_LEVEL_SLIGHT
import io.github.rosemoe.sora.util.MyCharacter
import io.github.rosemoe.sora.widget.SymbolPairMatch

/**
 * Tree Sitter language specification for Java.
 *
 * @author Akash Yadav
 */
class JavaLanguage(context: Context) :
  TreeSitterLanguage(context, TSLanguageJava.getInstance(), TS_TYPE) {

  companion object {

    const val TS_TYPE = "java"

    @JvmField
    val FACTORY = Factory { JavaLanguage(it) }
  }

  override val languageServer: ILanguageServer?
    get() = ILanguageServerRegistry.getDefault().getServer(JavaLanguageServer.SERVER_ID)

  override fun checkIsCompletionChar(c: Char): Boolean {
    return MyCharacter.isJavaIdentifierPart(c) || c == '.'
  }

  override fun getInterruptionLevel(): Int {
    return INTERRUPTION_LEVEL_SLIGHT
  }

  override fun getSymbolPairs(): SymbolPairMatch {
    return JavaSymbolPairs()
  }

  override fun createNewlineHandlers(): Array<TSBracketsHandler> {
    return arrayOf(TSCStyleBracketsHandler(this))
  }

  internal open class JavaSymbolPairs : CommonSymbolPairs() {
    init {
      super.putPair('<', SymbolPair("<", ">"))
    }
  }
}

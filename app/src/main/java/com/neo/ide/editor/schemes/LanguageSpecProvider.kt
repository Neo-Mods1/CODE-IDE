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



package com.neo.ide.editor.schemes

import android.content.Context
import com.neo.ide.editor.language.treesitter.TreeSitterLanguageSpec
import com.neo.ide.editor.language.treesitter.predicates.AnyOfPredicate
import com.neo.ide.editor.language.treesitter.predicates.EqualPredicate
import com.neo.ide.editor.language.treesitter.predicates.MatchPredicate
import com.neo.ide.editor.language.treesitter.predicates.NotEqualPredicate
import com.neo.ide.editor.language.treesitter.predicates.NotMatchPredicate
import com.neo.ide.treesitter.TSLanguage
import io.github.rosemoe.sora.editor.ts.LocalsCaptureSpec
import io.github.rosemoe.sora.editor.ts.TsLanguageSpec
import org.slf4j.LoggerFactory
import java.io.FileNotFoundException

/**
 * Provides language spec instances for tree sitter languages.
 *
 * @author Akash Yadav
 */
object LanguageSpecProvider {

  private const val BASE_SPEC_PATH = "editor/treesitter"
  private val log = LoggerFactory.getLogger(LanguageSpecProvider::class.java)

  @JvmStatic
  @JvmOverloads
  fun getLanguageSpec(
    context: Context,
    type: String,
    lang: TSLanguage,
    localsCaptureSpec: LocalsCaptureSpec = LocalsCaptureSpec.DEFAULT
  ): TreeSitterLanguageSpec {
    val editorLangSpec =
      TsLanguageSpec(
        language = lang,
        highlightScmSource = readScheme(context, type, "highlights"),
        localsScmSource = readScheme(context, type, "locals"),
        codeBlocksScmSource = readScheme(context, type, "blocks"),
        bracketsScmSource = readScheme(context, type, "brackets"),
        localsCaptureSpec = localsCaptureSpec,
        predicates =
        listOf(
          MatchPredicate,
          NotMatchPredicate,
          EqualPredicate,
          NotEqualPredicate,
          AnyOfPredicate
        )
      )
    return TreeSitterLanguageSpec(
      spec = editorLangSpec,
      indentsQueryScm = readScheme(context, type, "indents")
    )
  }

  private fun readScheme(context: Context, type: String, name: String): String {
    return try {
      context.assets.open("${BASE_SPEC_PATH}/${type}/${name}.scm").reader().readText()
    } catch (e: Exception) {
      if (e !is FileNotFoundException) {
        // log everything except FileNotFoundException
        log.error("Failed to read scheme file {} for type {}", name, type, e)
      }
      ""
    }
  }
}

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



package com.neo.ide.editor.language.treesitter.predicates

import com.neo.ide.treesitter.TSQuery
import com.neo.ide.treesitter.TSQueryMatch
import com.neo.ide.treesitter.TSQueryPredicateStep
import io.github.rosemoe.sora.editor.ts.predicate.PredicateResult
import io.github.rosemoe.sora.editor.ts.predicate.TsClientPredicateStep
import io.github.rosemoe.sora.editor.ts.predicate.TsPredicate
import io.github.rosemoe.sora.editor.ts.predicate.TsSyntheticCaptureContainer
import io.github.rosemoe.sora.editor.ts.predicate.builtin.getCaptureContent
import io.github.rosemoe.sora.editor.ts.predicate.builtin.parametersMatch
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.PatternSyntaxException

/**
 * [TsPredicate] implementation for '#match?' query predicates.
 *
 * @author Akash Yadav
 */
object MatchPredicate : TreeSitterPredicate() {

  override val name: String
    get() = "match"

  @JvmField
  val PARAMETERS =
    arrayOf(
      TSQueryPredicateStep.Type.String,
      TSQueryPredicateStep.Type.Capture,
      TSQueryPredicateStep.Type.String,
      TSQueryPredicateStep.Type.Done
    )

  private val cache = ConcurrentHashMap<String, Regex>()

  override fun doPredicateInternal(
    tsQuery: TSQuery,
    text: CharSequence,
    match: TSQueryMatch,
    predicateSteps: List<TsClientPredicateStep>,
    syntheticCaptures: TsSyntheticCaptureContainer
  ): PredicateResult {
    val captured = getCaptureContent(tsQuery, match, predicateSteps[1].content, text)
    try {
      var regex = cache[predicateSteps[2].content]
      if (regex == null) {
        regex = Regex(predicateSteps[2].content)
        cache[predicateSteps[2].content] = regex
      }
      for (str in captured) {
        if (regex.find(str) == null) {
          return PredicateResult.REJECT
        }
      }
      return PredicateResult.ACCEPT
    } catch (e: PatternSyntaxException) {
      e.printStackTrace()
      return PredicateResult.UNHANDLED
    }
  }

  override fun canHandle(steps: List<TsClientPredicateStep>): Boolean {
    return parametersMatch(steps, PARAMETERS)
  }
}

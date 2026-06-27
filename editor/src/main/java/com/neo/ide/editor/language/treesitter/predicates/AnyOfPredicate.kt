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

/**
 * [TsPredicate] implementation for '#any-of?' query predicates.
 *
 * Syntax : `"#any-of?" @capture "string" ["string", ... ] Done`
 *
 * Checks if the text of `@capture` matches (literally) any of the `"string"` defined.
 *
 * @author Akash Yadav
 */
object AnyOfPredicate : TreeSitterPredicate() {

  override val name: String
    get() = "any-of"

  override fun canHandle(steps: List<TsClientPredicateStep>): Boolean {
    return steps.size > 4 &&
        steps.let {
          it[0].predicateType == TSQueryPredicateStep.Type.String &&
              it[1].predicateType == TSQueryPredicateStep.Type.Capture &&
              it[it.lastIndex].predicateType == TSQueryPredicateStep.Type.Done &&
              it.subList(2, it.lastIndex - 1).all { step ->
                step.predicateType == TSQueryPredicateStep.Type.String
              }
        }
  }

  override fun doPredicateInternal(
    tsQuery: TSQuery,
    text: CharSequence,
    match: TSQueryMatch,
    predicateSteps: List<TsClientPredicateStep>,
    syntheticCaptures: TsSyntheticCaptureContainer
  ): PredicateResult {
    val captured = getCaptureContent(tsQuery, match, predicateSteps[1].content, text)
    val toMatch = predicateSteps.subList(2, predicateSteps.lastIndex - 1).map { it.content }
    for (capture in captured) {
      if (capture !in toMatch) {
        return PredicateResult.REJECT
      }
    }
    return PredicateResult.ACCEPT
  }
}

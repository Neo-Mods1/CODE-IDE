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
 * [TsPredicate] implementation for '#eq?' query predicates.
 *
 * Syntax : `"#eq?" @capture @capture | "string" Done`
 *
 * Checks if the contents of the first capture is equal to the given string or contents of the
 * second capture.
 *
 * @author Akash Yadav
 */
object EqualPredicate : TreeSitterPredicate() {

  override val name: String
    get() = "eq"

  override fun canHandle(steps: List<TsClientPredicateStep>): Boolean {
    return steps.size == 4 &&
        steps[0].predicateType == TSQueryPredicateStep.Type.String &&
        steps[1].predicateType == TSQueryPredicateStep.Type.Capture &&
        steps[2].predicateType.let {
          it == TSQueryPredicateStep.Type.Capture || it == TSQueryPredicateStep.Type.String
        } &&
        steps[3].predicateType == TSQueryPredicateStep.Type.Done
  }

  override fun doPredicateInternal(
    tsQuery: TSQuery,
    text: CharSequence,
    match: TSQueryMatch,
    predicateSteps: List<TsClientPredicateStep>,
    syntheticCaptures: TsSyntheticCaptureContainer
  ): PredicateResult {
    val first = getCaptureContent(tsQuery, match, predicateSteps[1].content, text)
    val second =
      predicateSteps[2].let {
        check(
          it.predicateType == TSQueryPredicateStep.Type.String ||
              it.predicateType == TSQueryPredicateStep.Type.Capture
        ) {
          "Second predicate step of #eq? predicate must be a string or a capture"
        }

        if (it.predicateType == TSQueryPredicateStep.Type.Capture) {
          getCaptureContent(tsQuery, match, it.content, text)
        } else {
          it.content
        }
      }

    return if (first == second) {
      PredicateResult.ACCEPT
    } else {
      PredicateResult.REJECT
    }
  }
}

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
import io.github.rosemoe.sora.editor.ts.predicate.PredicateResult
import io.github.rosemoe.sora.editor.ts.predicate.TsClientPredicateStep
import io.github.rosemoe.sora.editor.ts.predicate.TsSyntheticCaptureContainer

/**
 * A [TreeSitterPredicate] which inverts the result of another predicate.
 *
 * @author Akash Yadav
 */
open class InvertingPredicate(override val name: String,
  private val predicate: TreeSitterPredicate) :
  TreeSitterPredicate() {

  override fun canHandle(steps: List<TsClientPredicateStep>): Boolean {
    return predicate.canHandle(steps)
  }

  override fun doPredicateInternal(
    tsQuery: TSQuery,
    text: CharSequence,
    match: TSQueryMatch,
    predicateSteps: List<TsClientPredicateStep>,
    syntheticCaptures: TsSyntheticCaptureContainer
  ): PredicateResult {
    return when (val result = this.predicate.doPredicateInternal(tsQuery, text, match,
      predicateSteps, syntheticCaptures)) {
      PredicateResult.ACCEPT -> PredicateResult.REJECT
      PredicateResult.REJECT -> PredicateResult.ACCEPT
      else -> result
    }
  }
}

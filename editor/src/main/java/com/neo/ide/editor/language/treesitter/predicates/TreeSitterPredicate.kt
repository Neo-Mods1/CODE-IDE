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
import io.github.rosemoe.sora.editor.ts.predicate.TsPredicate
import io.github.rosemoe.sora.editor.ts.predicate.TsSyntheticCaptureContainer

/**
 * Base class for tree-sitter predicate implementations.
 *
 * @author Akash Yadav
 */
abstract class TreeSitterPredicate : TsPredicate {

  /** The name of the predicate that will be used to match. */
  abstract val name: String

  /**
   * Whether the implmentation can handle the given predicate steps.
   *
   * @param steps The predicate steps.
   * @return `true` if and only if the implementatin can handle the given predicate steps, `false`
   *   otherwise.
   */
  abstract fun canHandle(steps: List<TsClientPredicateStep>): Boolean

  /**
   * Performs the predicate check.
   *
   * @param tsQuery The [TSQuery] for the predicate.
   * @param text The editor text.
   * @param match The [TSQueryMatch] object.
   * @param predicateSteps The predicate steps.
   * @return The result of the predicate check.
   */
  internal abstract fun doPredicateInternal(
    tsQuery: TSQuery,
    text: CharSequence,
    match: TSQueryMatch,
    predicateSteps: List<TsClientPredicateStep>,
    syntheticCaptures: TsSyntheticCaptureContainer
  ): PredicateResult

  override fun doPredicate(
    tsQuery: TSQuery,
    text: CharSequence,
    match: TSQueryMatch,
    predicateSteps: List<TsClientPredicateStep>,
    syntheticCaptures: TsSyntheticCaptureContainer
  ): PredicateResult {

    if (
      predicateSteps.isEmpty() ||
      predicateSteps[0].content != "${name}?" ||
      !canHandle(predicateSteps)
    ) {
      return PredicateResult.UNHANDLED
    }

    return doPredicateInternal(tsQuery, text, match, predicateSteps, syntheticCaptures)
  }
}

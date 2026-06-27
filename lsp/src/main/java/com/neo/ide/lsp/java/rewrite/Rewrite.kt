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


package com.neo.ide.lsp.java.rewrite

import com.neo.ide.lsp.java.compiler.CompilerProvider
import com.neo.ide.lsp.models.CodeActionItem
import com.neo.ide.lsp.models.CodeActionKind
import com.neo.ide.lsp.models.DocumentChange
import com.neo.ide.lsp.models.TextEdit
import java.nio.file.Path

/**
 * A source code rewrite.
 *
 * @author Akash Yadav
 */
abstract class Rewrite {

  /**
   * Converts the edits to code action item.
   *
   * @param compiler The compiler service.
   * @param title The title for the code action.
   * @return The code action item.
   */
  fun asCodeActions(compiler: CompilerProvider, title: String): CodeActionItem? {
    val edits = rewrite(compiler)
    if (edits.isEmpty()) {
      return null
    }

    val changes: MutableList<DocumentChange> = ArrayList(0)
    for (file in edits.keys) {
      val textEdits = edits[file] ?: continue
      val change = DocumentChange()
      change.file = file
      change.edits = textEdits.asList()
      changes.add(change)
    }
    val action = CodeActionItem()
    action.title = title
    action.kind = CodeActionKind.QuickFix
    action.changes = changes
    finalizeCodeAction(action)
    return action
  }

  /**
   * Perform a rewrite across the entire codebase. The given compiler can be used for anything
   * except compiling other files. If you try to compile any file, the current thread will be
   * blocked.
   *
   * @param compiler The compiler.
   */
  abstract fun rewrite(compiler: CompilerProvider): Map<Path, Array<TextEdit>>

  /**
   * Called after the code action is created. Subclasses can implement this to do some finalization
   * tasks on the given code action.
   *
   * @param action The code action.
   */
  protected open fun finalizeCodeAction(action: CodeActionItem) {}

  companion object {

    /** CANCELLED signals that the rewrite couldn't be completed. */
    @JvmField var CANCELLED = emptyMap<Path, Array<TextEdit>>()
  }
}

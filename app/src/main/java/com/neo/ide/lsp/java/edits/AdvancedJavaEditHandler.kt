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



package com.neo.ide.lsp.java.edits

import com.neo.ide.lsp.java.JavaCompilerProvider
import com.neo.ide.lsp.java.compiler.JavaCompilerService
import com.neo.ide.lsp.models.CompletionItem
import com.neo.ide.projects.IProjectManager
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor
import java.nio.file.Path

/**
 * Handles edits for Java completion items.
 *
 * @author Akash Yadav
 */
abstract class AdvancedJavaEditHandler(protected val file: Path) : BaseJavaEditHandler() {

  override fun performEdits(
    item: CompletionItem,
    editor: CodeEditor,
    text: Content,
    line: Int,
    column: Int,
    index: Int
  ) {
    val compiler = JavaCompilerProvider.get(
      IProjectManager.getInstance().getWorkspace()?.findModuleForFile(file, false) ?: return
    )
    performEdits(compiler, editor, item)

    executeCommand(editor, item.command)
  }

  /**
   * Java edit handlers which require instance of the compiler should override this method instead
   * of [performEdits].
   *
   * @param compiler The compiler service instance.
   * @param editor The editor to perform edits on.
   * @param completionItem The completion item which contains required data.
   */
  abstract fun performEdits(
    compiler: JavaCompilerService,
    editor: CodeEditor,
    completionItem: CompletionItem
  )
}

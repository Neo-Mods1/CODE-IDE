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

import com.neo.ide.lsp.java.compiler.JavaCompilerService
import com.neo.ide.lsp.java.utils.EditHelper
import io.github.rosemoe.sora.widget.CodeEditor
import org.slf4j.LoggerFactory
import java.nio.file.Path

/**
 * Imports multiple classes at once.
 *
 * @param classes The fully qualified classnames to import.
 * @param imported The current imports of the given file.
 * @author Akash Yadav
 */
class MultipleClassImportEditHandler(
  private val classes: Set<String>,
  private val imported: Set<String>,
  file: Path
) : AdvancedJavaEditHandler(file) {

  companion object {

    private val log = LoggerFactory.getLogger(MultipleClassImportEditHandler::class.java)
  }

  override fun performEdits(
    compiler: JavaCompilerService,
    editor: CodeEditor,
    completionItem: com.neo.ide.lsp.models.CompletionItem
  ) {
    val edits = mutableListOf<com.neo.ide.lsp.models.TextEdit>()
    for (className in classes) {
      try {
        edits.addAll(EditHelper.addImportIfNeeded(compiler, file, imported, className))
      } catch (err: Throwable) {
        log.error("Unable to compute edits to perform import for class: {}", className)
      }
    }
    com.neo.ide.lsp.util.RewriteHelper.performEdits(edits, editor)
  }
}

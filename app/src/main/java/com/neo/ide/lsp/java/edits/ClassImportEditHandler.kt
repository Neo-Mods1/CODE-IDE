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
import com.neo.ide.lsp.models.ClassCompletionData
import com.neo.ide.lsp.models.CompletionItem
import com.neo.ide.lsp.util.RewriteHelper
import io.github.rosemoe.sora.widget.CodeEditor
import java.nio.file.Path

/**
 * Imports the required class for a ClassCompletionItem.
 *
 * @param imports The current file imports.
 * @param file The file in which this edit will be performed.
 * @author Akash Yadav
 */
class ClassImportEditHandler(val imports: Set<String>, file: Path) : AdvancedJavaEditHandler(file) {

  override fun performEdits(
    compiler: JavaCompilerService,
    editor: CodeEditor,
    completionItem: CompletionItem
  ) {
    val data = completionItem.data as? ClassCompletionData ?: return
    val className = data.className
    val edits = EditHelper.addImportIfNeeded(compiler, file, imports, className)

    if (edits.isNotEmpty()) {
      RewriteHelper.performEdits(edits, editor)
    }
  }
}

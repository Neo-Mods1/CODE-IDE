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



package com.neo.ide.lsp.java.providers.completion

import com.neo.ide.lsp.api.IServerSettings
import com.neo.ide.lsp.java.compiler.CompileTask
import com.neo.ide.lsp.java.compiler.JavaCompilerService
import com.neo.ide.lsp.models.CompletionItem
import com.neo.ide.lsp.models.CompletionResult
import com.neo.ide.progress.ProgressManager.Companion.abortIfCancelled
import openjdk.source.util.TreePath
import java.nio.file.Path

/** @author Akash Yadav */
class IdentifierCompletionProvider(
  completingFile: Path,
  cursor: Long,
  compiler: JavaCompilerService,
  settings: IServerSettings
) : IJavaCompletionProvider(cursor, completingFile, compiler, settings) {

  override fun doComplete(
    task: CompileTask,
    path: TreePath,
    partial: String,
    endsWithParen: Boolean,
  ): CompletionResult {
    val list = mutableListOf<CompletionItem>()

    abortIfCancelled()
    abortCompletionIfCancelled()

    val snippets =
      SnippetCompletionProvider(cursor, file, compiler, settings)
        .complete(task, path, partial, endsWithParen)
    list.addAll(snippets.items)

    val scopeMembers =
      ScopeCompletionProvider(file, cursor, compiler, settings)
        .complete(task, path, partial, endsWithParen)
    list.addAll(scopeMembers.items)

    abortIfCancelled()
    abortCompletionIfCancelled()
    val staticImports =
      StaticImportCompletionProvider(file, cursor, compiler, settings, path.compilationUnit)
        .complete(task, path, partial, endsWithParen)
    list.addAll(staticImports.items)

    if (CompletionResult.TRIM_TO_MAX && list.size < CompletionResult.MAX_ITEMS) {
      val allLower: Boolean = settings.shouldMatchAllLowerCase()
      if (allLower || partial.isNotEmpty() && Character.isUpperCase(partial[0])) {
        abortIfCancelled()
        abortCompletionIfCancelled()
        val classNames =
          ClassNamesCompletionProvider(file, cursor, compiler, settings, path.compilationUnit)
            .complete(task, path, partial, endsWithParen)
        list.addAll(classNames.items)
      }
    }

    abortIfCancelled()
    abortCompletionIfCancelled()
    val keywords =
      KeywordCompletionProvider(file, cursor, compiler, settings)
        .complete(task, path, partial, endsWithParen)
    list.addAll(keywords.items)

    return CompletionResult(list)
  }
}

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
import com.neo.ide.lsp.java.providers.snippet.JavaSnippetRepository
import com.neo.ide.lsp.java.providers.snippet.JavaSnippetScope
import com.neo.ide.lsp.models.CompletionItem
import com.neo.ide.lsp.models.CompletionResult
import com.neo.ide.lsp.models.MatchLevel
import com.neo.ide.lsp.snippets.ISnippet
import com.neo.ide.preferences.internal.EditorPreferences
import io.github.rosemoe.sora.text.TextUtils
import openjdk.source.tree.ClassTree
import openjdk.source.tree.CompilationUnitTree
import openjdk.source.tree.MethodTree
import openjdk.source.util.TreePath
import java.nio.file.Path

/**
 * Provides snippet completion for Java files.
 *
 * @author Akash Yadav
 */
class SnippetCompletionProvider(
  cursor: Long,
  completingFile: Path,
  compiler: JavaCompilerService,
  settings: IServerSettings
) : IJavaCompletionProvider(cursor, completingFile, compiler, settings) {

  override fun doComplete(
    task: CompileTask,
    path: TreePath,
    partial: String,
    endsWithParen: Boolean
  ): CompletionResult {
    val scope = findSnippetScope(path) ?: return CompletionResult.EMPTY
    val indent = spacesBeforeCursor(task.root().sourceFile.getCharContent(true))
    val snippets = mutableListOf<ISnippet>()

    // add global snippets, if any
    JavaSnippetRepository.snippets[JavaSnippetScope.GLOBAL]?.let { snippets.addAll(it) }

    val snippetScope =
      when (scope.leaf) {
        is CompilationUnitTree -> JavaSnippetScope.TOP_LEVEL
        is ClassTree -> JavaSnippetScope.MEMBER
        is MethodTree -> JavaSnippetScope.LOCAL
        else -> null
      }

    // add snippets for the current scope
    snippetScope?.let { JavaSnippetRepository.snippets[it]?.let { list -> snippets.addAll(list) } }

    val items = mutableListOf<CompletionItem>()

    for (snippet in snippets) {
      val matchLevel = matchLevel(snippet.prefix, partial)
      if (matchLevel == MatchLevel.NO_MATCH) {
        continue
      }

      items.add(snippetItem(snippet, matchLevel, partial, indent))
    }

    return CompletionResult(items)
  }

  private fun spacesBeforeCursor(charContent: CharSequence?): Int {
    charContent ?: return 0
    var start = cursor.toInt()
    while (start >= 0) {
      val c = charContent[start]
      if (c == '\n' || !c.isWhitespace()) {
        break
      }
      --start
    }
    return TextUtils.countLeadingSpaceCount(charContent.substring(start, cursor.toInt()),
      EditorPreferences.tabSize)
  }

  private fun findSnippetScope(path: TreePath?): TreePath? {
    var scope = path
    while (scope != null) {
      if (scope.leaf.let { it is CompilationUnitTree || it is ClassTree || it is MethodTree }) {
        return scope
      }
      scope = scope.parentPath
    }
    return null
  }
}

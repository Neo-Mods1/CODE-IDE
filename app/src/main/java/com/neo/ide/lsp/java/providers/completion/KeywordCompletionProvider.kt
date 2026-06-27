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
import com.neo.ide.lsp.models.MatchLevel.NO_MATCH
import com.neo.ide.progress.ProgressManager.Companion.abortIfCancelled
import openjdk.source.tree.ClassTree
import openjdk.source.tree.CompilationUnitTree
import openjdk.source.tree.MethodTree
import openjdk.source.tree.Tree
import openjdk.source.util.TreePath
import java.nio.file.Path

/**
 * Provides keyword completions.
 *
 * @author Akash Yadav
 */
class KeywordCompletionProvider(
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

    if (partial.isBlank()) {
      return CompletionResult.EMPTY
    }

    val level: Tree = findKeywordLevel(path)
    var keywords = arrayOf<String>()
    when (level) {
      is CompilationUnitTree -> keywords = TOP_LEVEL_KEYWORDS
      is ClassTree -> keywords = CLASS_BODY_KEYWORDS
      is MethodTree -> keywords = METHOD_BODY_KEYWORDS
    }

    abortIfCancelled()
    abortCompletionIfCancelled()
    val list = mutableListOf<CompletionItem>()
    for (k in keywords) {
      val matchLevel = matchLevel(k, partial)
      if (matchLevel == NO_MATCH) {
        continue
      }

      list.add(keyword(k, partial, 100))
    }

    return CompletionResult(list)
  }

  private fun findKeywordLevel(treePath: TreePath): Tree {
    var path: TreePath? = treePath
    while (path != null) {
      if (path.leaf is CompilationUnitTree || path.leaf is ClassTree || path.leaf is MethodTree) {
        return path.leaf
      }
      path = path.parentPath
    }
    throw RuntimeException("empty path")
  }

  companion object {
    private val TOP_LEVEL_KEYWORDS =
      arrayOf(
        "package",
        "import",
        "public",
        "private",
        "protected",
        "abstract",
        "class",
        "interface",
        "@interface",
        "extends",
        "implements"
      )
    private val CLASS_BODY_KEYWORDS =
      arrayOf(
        "public",
        "private",
        "protected",
        "static",
        "final",
        "native",
        "synchronized",
        "abstract",
        "default",
        "class",
        "interface",
        "void",
        "boolean",
        "int",
        "long",
        "float",
        "double",
        "true",
        "false",
        "null"
      )
    private val METHOD_BODY_KEYWORDS =
      arrayOf(
        "new",
        "assert",
        "try",
        "catch",
        "finally",
        "throw",
        "return",
        "break",
        "case",
        "continue",
        "default",
        "do",
        "while",
        "for",
        "switch",
        "if",
        "else",
        "instanceof",
        "var",
        "final",
        "class",
        "void",
        "boolean",
        "int",
        "long",
        "float",
        "double",
        "synchronized",
        "true",
        "false",
        "null"
      )
  }
}

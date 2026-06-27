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
import com.neo.ide.lsp.java.providers.CompletionProvider
import com.neo.ide.lsp.models.CompletionItem
import com.neo.ide.lsp.models.CompletionResult
import com.neo.ide.lsp.models.MatchLevel.NO_MATCH
import com.neo.ide.progress.ProgressManager.Companion.abortIfCancelled
import openjdk.source.tree.ClassTree
import openjdk.source.tree.CompilationUnitTree
import openjdk.source.util.TreePath
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Objects

/**
 * Completes class names.
 *
 * @author Akash Yadav
 */
class ClassNamesCompletionProvider(
  completingFile: Path,
  cursor: Long,
  compiler: JavaCompilerService,
  settings: IServerSettings,
  val root: CompilationUnitTree,
) : IJavaCompletionProvider(cursor, completingFile, compiler, settings) {

  override fun doComplete(
    task: CompileTask,
    path: TreePath,
    partial: String,
    endsWithParen: Boolean,
  ): CompletionResult {
    val list = mutableListOf<CompletionItem>()
    val packageName = Objects.toString(root.packageName, "")
    val uniques: MutableSet<String> = HashSet()

    val file: Path = Paths.get(root.sourceFile.toUri())
    val imports: Set<String> =
      root.imports.map { it.qualifiedIdentifier }.mapNotNull { it.toString() }.toSet()

    abortIfCancelled()
    abortCompletionIfCancelled()
    for (className in compiler.packagePrivateTopLevelTypes(packageName)) {
      val matchLevel = matchLevel(className, partial)
      if (matchLevel == NO_MATCH) {
        continue
      }

      list.add(classItem(imports, file, className, matchLevel))
      uniques.add(className)
    }

    abortIfCancelled()
    abortCompletionIfCancelled()

    val topLevelTypes = compiler.publicTopLevelTypes()
    for (className in topLevelTypes) {
      val matchLevel = matchLevel(simpleName(className), partial)
      if (matchLevel == NO_MATCH) {
        continue
      }

      if (uniques.contains(className)) {
        continue
      }

      list.add(classItem(imports, file, className, matchLevel))
      uniques.add(className)
    }
    abortIfCancelled()
    abortCompletionIfCancelled()
    for (t in root.typeDecls) {
      if (t !is ClassTree) {
        continue
      }
      val candidate = if (t.simpleName == null) "" else t.simpleName

      val matchLevel = matchLevel(candidate, partial)
      if (matchLevel == NO_MATCH) {
        continue
      }

      val name = packageName + "." + t.simpleName
      list.add(classItem(name, matchLevel))

      if (list.size > CompletionProvider.MAX_COMPLETION_ITEMS) {
        break
      }
    }

    log.info("...found {} class names", list.size)

    return CompletionResult(list)
  }
}

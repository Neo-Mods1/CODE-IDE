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
import com.neo.ide.lsp.models.MatchLevel
import com.neo.ide.lsp.models.MatchLevel.NO_MATCH
import com.neo.ide.progress.ProgressManager.Companion.abortIfCancelled
import jdkx.lang.model.element.Element
import jdkx.lang.model.element.ElementKind.METHOD
import jdkx.lang.model.element.ExecutableElement
import jdkx.lang.model.element.Modifier.STATIC
import jdkx.lang.model.element.Name
import jdkx.lang.model.element.TypeElement
import openjdk.source.tree.CompilationUnitTree
import openjdk.source.tree.MemberSelectTree
import openjdk.source.util.TreePath
import openjdk.source.util.Trees
import java.nio.file.Path

/**
 * Completes static imports.
 *
 * @author Akash Yadav
 */
class StaticImportCompletionProvider(
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
    val trees = Trees.instance(task.task)
    val methods = mutableMapOf<String, MutableList<ExecutableElement>>()
    val matchRatios: MutableMap<String, MatchLevel> = mutableMapOf()

    abortIfCancelled()
    abortCompletionIfCancelled()

    outer@ for (i in root.imports) {
      if (!i.isStatic) {
        continue
      }

      val id = i.qualifiedIdentifier as MemberSelectTree
      if (!importMatchesPartial(id.identifier, partial)) {
        continue
      }

      val exprPath = trees.getPath(root, id.expression)
      val type = trees.getElement(exprPath) as TypeElement

      for (member in type.enclosedElements) {
        if (!member.modifiers.contains(STATIC)) {
          continue
        }

        if (!memberMatchesImport(id.identifier, member)) {
          continue
        }

        val matchLevel = matchLevel(member.simpleName, partial)
        if (matchLevel == NO_MATCH) {
          continue
        }

        if (member.kind == METHOD) {
          putMethod(member as ExecutableElement, methods)
          matchRatios.putIfAbsent(member.simpleName.toString(), matchLevel)
        } else {
          list.add(item(task, member, matchLevel))
        }
        if (list.size + methods.size > CompletionProvider.MAX_COMPLETION_ITEMS) {
          break@outer
        }
      }
    }

    for ((key, value) in methods) {
      val matchLevel = matchRatios.getOrDefault(key, NO_MATCH)
      if (matchLevel == NO_MATCH) {
        continue
      }

      list.add(method(task, value, !endsWithParen, matchLevel, partial))
    }

    log.info("...found {} static imports", list.size)

    return CompletionResult(list)
  }

  private fun importMatchesPartial(staticImport: Name, partial: String): Boolean {
    return (staticImport.contentEquals("*") || matchLevel(staticImport, partial) != NO_MATCH)
  }

  private fun memberMatchesImport(staticImport: Name, member: Element): Boolean {
    return staticImport.contentEquals("*") || staticImport.contentEquals(member.simpleName)
  }
}

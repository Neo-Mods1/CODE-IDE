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
import com.neo.ide.lsp.models.CompletionResult
import com.neo.ide.lsp.models.MatchLevel.NO_MATCH
import com.neo.ide.progress.ProgressManager.Companion.abortIfCancelled
import jdkx.lang.model.element.ElementKind.ENUM
import jdkx.lang.model.element.ElementKind.ENUM_CONSTANT
import jdkx.lang.model.element.TypeElement
import jdkx.lang.model.type.DeclaredType
import openjdk.source.tree.SwitchTree
import openjdk.source.util.TreePath
import openjdk.source.util.Trees
import java.nio.file.Path

/**
 * Provides completions for switch constants.
 *
 * @author Akash Yadav
 */
class SwitchConstantCompletionProvider(
  completingFile: Path,
  cursor: Long,
  compiler: JavaCompilerService,
  settings: IServerSettings,
) : IJavaCompletionProvider(cursor, completingFile, compiler, settings) {

  override fun doComplete(
    task: CompileTask,
    path: TreePath,
    partial: String,
    endsWithParen: Boolean,
  ): CompletionResult {
    val switchTree = path.leaf as SwitchTree
    val exprPath = TreePath(path, switchTree.expression)
    val type = Trees.instance(task.task).getTypeMirror(exprPath)

    if (type.kind.isPrimitive || type !is DeclaredType) {
      // primitive types do not have any members
      return completeIdentifier(task, exprPath, partial, endsWithParen)
    }

    val element = type.asElement() as TypeElement

    if (element.kind != ENUM) {
      // If the switch's expression is not an enum type
      // we will not get any constants to complete
      // In this case, we fall back to completing identifiers
      // At this point, we are sure that the case expression will definitely be an identifier
      // tree
      // see visitCase (CaseTree, Long) in FindCompletionsAt.java
      return completeIdentifier(task, exprPath, partial, endsWithParen)
    }

    log.info("...complete constants of type {}", type)

    val list: MutableList<com.neo.ide.lsp.models.CompletionItem> = ArrayList()

    abortIfCancelled()
    abortCompletionIfCancelled()

    for (member in task.task.elements.getAllMembers(element)) {
      if (member.kind != ENUM_CONSTANT) {
        continue
      }

      val matchLevel = matchLevel(member.simpleName, partial)
      if (matchLevel == NO_MATCH) {
        continue
      }

      list.add(item(task, member, matchLevel))
    }

    return CompletionResult(list)
  }

  private fun completeIdentifier(
    task: CompileTask,
    path: TreePath,
    partial: String,
    endsWithParen: Boolean
  ): CompletionResult {
    abortIfCancelled()
    abortCompletionIfCancelled()
    return IdentifierCompletionProvider(file, cursor, compiler, settings)
      .complete(task, path, partial, endsWithParen)
  }
}

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
import com.neo.ide.lsp.java.utils.ScopeHelper
import com.neo.ide.lsp.models.CompletionItem
import com.neo.ide.lsp.models.CompletionResult
import com.neo.ide.lsp.models.MatchLevel
import com.neo.ide.lsp.models.MatchLevel.NO_MATCH
import com.neo.ide.progress.ProgressManager.Companion.abortIfCancelled
import jdkx.lang.model.element.ElementKind.CONSTRUCTOR
import jdkx.lang.model.element.ElementKind.METHOD
import jdkx.lang.model.element.ExecutableElement
import jdkx.lang.model.element.Modifier.STATIC
import jdkx.lang.model.element.TypeElement
import jdkx.lang.model.type.ArrayType
import jdkx.lang.model.type.DeclaredType
import jdkx.lang.model.type.TypeVariable
import openjdk.source.tree.MemberSelectTree
import openjdk.source.tree.Scope
import openjdk.source.util.TreePath
import openjdk.source.util.Trees
import openjdk.tools.javac.code.Symbol
import java.nio.file.Path

/**
 * Completions for member select.
 *
 * @author Akash Yadav
 */
class MemberSelectCompletionProvider(
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
    val trees = Trees.instance(task.task)
    val select =
      path.leaf as? MemberSelectTree
        ?: run {
          log.error("A member select tree was expected but was {}", path.leaf.javaClass)
          return CompletionResult.EMPTY
        }

    log.info("...complete members of {}", select.expression)

    val exprPath = TreePath(path, select.expression)
    val isStatic = trees.getElement(exprPath) is TypeElement
    val scope = trees.getScope(exprPath)

    abortIfCancelled()
    abortCompletionIfCancelled()
    return when (val type = trees.getTypeMirror(exprPath)) {
      is ArrayType -> completeArrayMemberSelect(isStatic, partial)
      is TypeVariable ->
        completeTypeVariableMemberSelect(task, scope, type, isStatic, partial, endsWithParen)

      is DeclaredType ->
        completeDeclaredTypeMemberSelect(task, scope, type, isStatic, partial, endsWithParen)

      else -> CompletionResult.EMPTY
    }
  }

  private fun completeArrayMemberSelect(
    isStatic: Boolean,
    partialName: CharSequence
  ): CompletionResult {
    return if (isStatic) {
      abortIfCancelled()
      abortCompletionIfCancelled()
      CompletionResult.EMPTY
    } else {
      val list = mutableListOf<CompletionItem>()
      list.add(keyword("length", partialName, 100))
      CompletionResult(list)
    }
  }

  private fun completeTypeVariableMemberSelect(
    task: CompileTask,
    scope: Scope,
    type: TypeVariable,
    isStatic: Boolean,
    partial: String,
    endsWithParen: Boolean,
  ): CompletionResult {
    abortIfCancelled()
    abortCompletionIfCancelled()
    return when (type.upperBound) {
      is DeclaredType ->
        completeDeclaredTypeMemberSelect(
          task,
          scope,
          type.upperBound as DeclaredType,
          isStatic,
          partial,
          endsWithParen
        )

      is TypeVariable ->
        completeTypeVariableMemberSelect(
          task,
          scope,
          type.upperBound as TypeVariable,
          isStatic,
          partial,
          endsWithParen
        )

      else -> CompletionResult.EMPTY
    }
  }

  private fun completeDeclaredTypeMemberSelect(
    task: CompileTask,
    scope: Scope,
    type: DeclaredType,
    isStatic: Boolean,
    partial: String,
    endsWithParen: Boolean,
  ): CompletionResult {
    val trees = Trees.instance(task.task)
    val typeElement = type.asElement() as TypeElement
    val list = mutableListOf<CompletionItem>()
    val methods = mutableMapOf<String, MutableList<ExecutableElement>>()
    val matchLevels = mutableMapOf<String, MatchLevel>()

    log.debug("DeclaredType {} with members {} in scope: {}",
      typeElement,
      (typeElement as Symbol).members(),
      scope
    )

    abortIfCancelled()
    abortCompletionIfCancelled()
    for (member in task.task.elements.getAllMembers(typeElement)) {
      if (member.kind == CONSTRUCTOR) {
        continue
      }
      val matchLevel = matchLevel(member.simpleName, partial)
      if (matchLevel == NO_MATCH) {
        continue
      }

      if (!trees.isAccessible(scope, member, type)) {
        continue
      }

      if (isStatic != member.modifiers.contains(STATIC)) {
        continue
      }

      if (member.kind == METHOD) {
        putMethod((member as ExecutableElement), methods)
        matchLevels.putIfAbsent(member.getSimpleName().toString(), matchLevel)
      } else {
        list.add(item(task, member, matchLevel))
      }
    }

    log.debug("Found {} members along with {} methods", list.size, methods.size)

    abortIfCancelled()
    abortCompletionIfCancelled()
    for ((key, value) in methods) {
      val matchLevel = matchLevels.getOrDefault(key, NO_MATCH)
      if (matchLevel == NO_MATCH) {
        continue
      }

      list.add(method(task, value, !endsWithParen, matchLevel, partial))
    }

    if (isStatic) {
      list.add(keyword("class", partial, 100))
    }

    if (!isStatic && isEnclosingClass(type, scope)) {
      list.add(keyword("this", partial, 100))
      list.add(keyword("super", partial, 100))
    }

    return CompletionResult(list)
  }

  private fun isEnclosingClass(type: DeclaredType, start: Scope): Boolean {
    for (s in ScopeHelper.fastScopes(start)) {
      // If we reach a static method, stop looking
      val method = s.enclosingMethod
      if (method != null && method.modifiers.contains(STATIC)) {
        return false
      }

      // If we find the enclosing class
      val thisElement = s.enclosingClass
      if (thisElement != null && thisElement.asType() == type) {
        return true
      }

      // If the enclosing class is static, stop looking
      if (thisElement != null && thisElement.modifiers.contains(STATIC)) {
        return false
      }
    }
    return false
  }
}

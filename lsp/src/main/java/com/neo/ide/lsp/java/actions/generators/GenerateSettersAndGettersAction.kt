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


package com.neo.ide.lsp.java.actions.generators

import android.content.Context
import com.blankj.utilcode.util.ThreadUtils
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.stmt.BlockStmt
import com.github.javaparser.ast.type.Type
import com.github.javaparser.ast.type.VoidType
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.requireFile
import com.neo.ide.actions.requirePath
import com.neo.ide.lsp.java.JavaCompilerProvider
import com.neo.ide.lsp.java.actions.FieldBasedAction
import com.neo.ide.lsp.java.compiler.CompileTask
import com.neo.ide.lsp.java.utils.EditHelper
import com.neo.ide.lsp.java.utils.JavaParserUtils
import com.neo.ide.lsp.java.utils.TypeUtils.toType
import com.neo.ide.preferences.internal.EditorPreferences
import com.neo.ide.preferences.utils.indentationString
import com.neo.ide.projects.IProjectManager
import com.neo.ide.resources.R
import com.neo.ide.utils.flashError
import io.github.rosemoe.sora.widget.CodeEditor
import jdkx.lang.model.element.Modifier.FINAL
import jdkx.lang.model.element.VariableElement
import openjdk.source.tree.ClassTree
import openjdk.source.util.TreePath
import openjdk.source.util.Trees
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

/**
 * Allows the user to select fields from the current class, then generates setters and getters for
 * the selected fields.
 *
 * @author Akash Yadav
 */
class GenerateSettersAndGettersAction : FieldBasedAction() {

  override val id: String = "ide.editor.lsp.java.generator.settersAndGetters"
  override var label: String = ""

  override val titleTextRes: Int = R.string.action_generate_setters_getters

  companion object {

    private val log = LoggerFactory.getLogger(GenerateSettersAndGettersAction::class.java)
  }

  override fun onGetFields(fields: List<String>, data: ActionData) {

    showFieldSelector(fields, data) { checkedNames ->
      CompletableFuture.runAsync { generateForFields(data, checkedNames) }
        .whenComplete {
            _, error,
          ->
          if (error != null) {
            log.error("Unable to generate setters and getters", error)
            ThreadUtils.runOnUiThread {
              flashError(
                data[Context::class.java]!!.getString(R.string.msg_cannot_generate_setters_getters)
              )
            }
            return@whenComplete
          }
        }
    }
  }

  private fun generateForFields(data: ActionData, names: MutableSet<String>) {
    val compiler =
      JavaCompilerProvider.get(
        IProjectManager.getInstance().getWorkspace()?.findModuleForFile(data.requireFile(), false)
          ?: return
      )
    val range = data[com.neo.ide.models.Range::class.java]!!
    val file = data.requirePath()

    compiler.compile(file).run { task ->
      val triple = findFields(task, file, range)
      val typeFinder = triple.first
      val type = triple.second
      val fields = triple.third

      fields.removeIf { !names.contains("${it.name}: ${it.type}") }

      log.debug("Creating setters/getters for fields: {}", fields.map { it.name })

      generateForFields(data, task, type, fields.map { TreePath(typeFinder.path, it) })
    }
  }

  private fun generateForFields(
    data: ActionData,
    task: CompileTask,
    type: ClassTree,
    paths: List<TreePath>,
  ) {
    val file = data.requirePath()
    val editor = data[CodeEditor::class.java]!!
    val trees = Trees.instance(task.task)
    val insert = EditHelper.insertAtEndOfClass(task.task, task.root(file), type)
    val sb = StringBuilder()

    for (path in paths) {
      val element = trees.getElement(path) ?: continue
      if (element !is VariableElement) {
        continue
      }

      val leaf = path.leaf
      val indent = EditHelper.indent(task.task, task.root(file), leaf) + EditorPreferences.tabSize
      sb.append(createGetter(element, indent))
      if (!element.modifiers.contains(FINAL)) {
        sb.append(createSetter(element, indent))
      }
    }

    ThreadUtils.runOnUiThread {
      editor.text.insert(insert.line, insert.column, sb)
      editor.formatCodeAsync()
    }
  }

  private fun createGetter(variable: VariableElement, indent: Int): String {
    val name = variable.simpleName.toString()
    val method =
      createMethod(variable, "get", toType(variable.asType())) { _, body ->
        body.addStatement(createReturnStmt(name))
      }
    var text = "\n" + JavaParserUtils.prettyPrint(method) { false }
    text = text.replace("\n", "\n${indentationString(indent)}")

    return text
  }

  private fun createReturnStmt(name: String) = StaticJavaParser.parseStatement("return this.$name;")

  private fun createSetter(variable: VariableElement, indent: Int): String {
    val name: String = variable.simpleName.toString()
    val method =
      createMethod(variable, "set", VoidType()) { method, body ->
        method.addParameter(toType(variable.asType()), name)
        body.addStatement(createAssignmentStmt(name))
      }

    var text = "\n" + JavaParserUtils.prettyPrint(method) { false }
    text = text.replace("\n", "\n${indentationString(indent)}")

    return text
  }

  private fun createMethod(
    variable: VariableElement,
    prefix: String,
    returnType: Type,
    vararg modifiers: Modifier.Keyword = arrayOf(Modifier.Keyword.PUBLIC),
    block: (MethodDeclaration, BlockStmt) -> Unit
  ): MethodDeclaration {
    val name = variable.simpleName.toString()
    val method = MethodDeclaration()
    val body = method.createBody()
    method.name = SimpleName(createName(name, prefix))
    method.type = returnType
    method.addModifier(*modifiers)
    block(method, body)
    return method
  }

  private fun createAssignmentStmt(name: String) =
    StaticJavaParser.parseStatement("this.$name = $name;")

  private fun createName(name: String, prefix: String): String {
    val sb = StringBuilder(name)
    sb.setCharAt(0, Character.toUpperCase(sb[0]))
    sb.insert(0, prefix)
    return sb.toString()
  }
}

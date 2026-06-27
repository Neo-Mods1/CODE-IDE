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
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.requireFile
import com.neo.ide.actions.requirePath
import com.neo.ide.lsp.java.JavaCompilerProvider
import com.neo.ide.lsp.java.actions.FieldBasedAction
import com.neo.ide.lsp.java.compiler.CompileTask
import com.neo.ide.lsp.java.utils.EditHelper
import com.neo.ide.lsp.java.utils.ShortTypePrinter.NO_PACKAGE
import com.neo.ide.preferences.utils.indentationString
import com.neo.ide.projects.IProjectManager
import com.neo.ide.resources.R.string
import com.neo.ide.utils.flashError
import io.github.rosemoe.sora.widget.CodeEditor
import openjdk.source.tree.ClassTree
import openjdk.source.tree.VariableTree
import openjdk.source.util.TreePath
import openjdk.tools.javac.api.JavacTrees
import openjdk.tools.javac.code.Symbol.ClassSymbol
import openjdk.tools.javac.code.Symbol.VarSymbol
import openjdk.tools.javac.code.Type
import openjdk.tools.javac.tree.JCTree
import openjdk.tools.javac.tree.TreeInfo
import openjdk.tools.javac.util.ListBuffer
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

/**
 * Allows the user to select fields and generate a constructor which has parameters same as the
 * selected fields.
 *
 * The generated constructor has statements which assigns the fields to the parameter types.
 *
 * @author Akash Yadav
 */
class GenerateConstructorAction : FieldBasedAction() {

  override val titleTextRes: Int = string.action_generate_constructor
  override val id: String = "ide.editor.lsp.java.generator.constructor"
  override var label: String = ""

  companion object {

    private val log = LoggerFactory.getLogger(GenerateConstructorAction::class.java)
  }

  override fun onGetFields(fields: List<String>, data: ActionData) {
    showFieldSelector(fields, data) { selected ->
      CompletableFuture.runAsync { generateConstructor(data, selected) }
        .whenComplete { _, error ->
          if (error != null) {
            log.error("Unable to generate constructor for the selected fields", error)
            flashError(string.msg_cannot_generate_constructor)
          }
        }
    }
  }

  private fun generateConstructor(data: ActionData, selected: MutableSet<String>) {
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

      fields.removeIf { !selected.contains("${it.name}: ${it.type}") }

      log.debug("Creating toString() method with fields: {}", fields.map { it.name })

      generateForFields(data, task, type, fields.map { TreePath(typeFinder.path, it) })
    }
  }

  private fun generateForFields(
    data: ActionData,
    task: CompileTask,
    type: ClassTree,
    paths: List<TreePath>
  ) {
    val editor = data[CodeEditor::class.java]!!
    val trees = JavacTrees.instance(task.task)
    val sym = TreeInfo.symbolFor(type as JCTree) as ClassSymbol
    val varTypes = mapTypes(paths)
    val varNames = paths.map { it.leaf as VariableTree }.map { it.name.toString() }

    if (paths.isEmpty() || trees.findConstructor(sym, varTypes) != null) {
      log.warn(
        "A constructor with same parameter types is already available in class {}", type.simpleName
      )
      flashError(data[Context::class.java]!!.getString(string.msg_constructor_available))
      return
    }

    val stopWatch = com.neo.ide.utils.StopWatch("generateConstructorForFields()")
    val constructor =
      newConstructor(type.simpleName.toString(), varTypes.toTypedArray(), varNames.toTypedArray())
    val body = constructor.createBody()
    for (varName in varNames) {
      body.addStatement(StaticJavaParser.parseStatement("this.$varName = $varName;"))
    }

    stopWatch.lap("Constructor generated")
    log.info("Inserting constructor into editor...")

    val insertAt = EditHelper.insertAfter(task.task, task.root(), paths.last().leaf)
    val indent = EditHelper.indent(task.task, task.root(), paths.last().leaf)
    var text = constructor.toString()
    text = text.replace("\n", "\n${indentationString(indent)}")
    text += "\n"

    ThreadUtils.runOnUiThread {
      editor.text.insert(insertAt.line, insertAt.column, text)
      editor.formatCodeAsync()
      stopWatch.log()
    }
  }

  private fun newConstructor(
    name: String,
    paramTypes: Array<Type>,
    paramNames: Array<String>
  ): ConstructorDeclaration {
    val constructor = ConstructorDeclaration()
    constructor.setName(name)
    constructor.addModifier(Modifier.Keyword.PUBLIC)

    for (i in paramTypes.indices) {
      val paramType = paramTypes[i]
      val paramName = paramNames[i]

      constructor.addParameter(NO_PACKAGE.print(paramType), paramName)
    }

    return constructor
  }

  private fun mapTypes(paths: List<TreePath>): openjdk.tools.javac.util.List<Type> {
    val buffer = ListBuffer<Type>()
    for (path in paths) {
      val leaf = path.leaf
      val sym = TreeInfo.symbolFor(leaf as JCTree) as VarSymbol
      buffer.add(sym.type)
    }

    return buffer.toList()
  }
}

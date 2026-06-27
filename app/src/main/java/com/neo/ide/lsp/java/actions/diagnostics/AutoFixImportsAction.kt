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



package com.neo.ide.lsp.java.actions.diagnostics

import com.neo.ide.actions.ActionData
import com.neo.ide.actions.requireContext
import com.neo.ide.actions.requirePath
import com.neo.ide.lsp.java.R
import com.neo.ide.lsp.java.actions.BaseJavaCodeAction
import com.neo.ide.lsp.java.compiler.CompileTask
import com.neo.ide.lsp.java.models.DiagnosticCode
import com.neo.ide.lsp.java.utils.positionForImports
import com.neo.ide.lsp.models.CodeActionItem
import com.neo.ide.lsp.models.CodeActionKind
import com.neo.ide.lsp.models.DocumentChange
import com.neo.ide.lsp.models.TextEdit
import com.neo.ide.models.Range
import com.neo.ide.utils.DialogUtils
import com.neo.ide.utils.flashInfo
import org.slf4j.LoggerFactory
import java.nio.file.Path

/**
 * Analyzes the source file for unresolved names and tries to import all of them at once.
 *
 * @author Akash Yadav
 */
class AutoFixImportsAction : BaseJavaCodeAction() {

  override val titleTextRes: Int = R.string.title_fix_imports
  override val id: String = "ide.editor.lsp.java.diagnostics.autoFixImports"
  override var label: String = ""

  companion object {

    private val log = LoggerFactory.getLogger(AutoFixImportsAction::class.java)
  }

  override suspend fun execAction(data: ActionData): Result {
    val path = data.requirePath()
    val compiler = data.requireCompiler()
    return compiler.compile(path).get { task ->
      val classes = mutableMapOf<String, List<String>>()

      // find all unresolved simple names
      unresolvedNames(path, task).forEach { simpleName ->

        // if we have already looked for this simple name
        // we do not need to look it up again
        if (classes[simpleName] != null) return@forEach

        // find classes with those names
        compiler.findQualifiedNames(simpleName).let { names ->

          // if we find classes with that specific simple name, map them to the simple name
          if (names.isNotEmpty()) {
            classes[simpleName] = names
          }
        }
      }

      // return the result
      Result(getFileImports(task, path), classes)
    }
  }

  override fun postExec(data: ActionData, result: Any) {
    if (result !is Result) {
      log.error("Invalid result returned from execAction: {}", result)
      return
    }

    if (result.classes.isEmpty()) {
      flashInfo(R.string.msg_no_unresolved_classes)
      return
    }

    // if there are multiple classes with same simple name
    // ask the user to choose the appropriate class
    if (result.classes.any { it.value.size > 1 }) {
      finalizeClassNames(data, result)
    } else {
      performEdits(data, result)
    }
  }

  private fun finalizeClassNames(data: ActionData, result: Result) {
    var e: Map.Entry<String, List<String>>? = null
    for (entry in result.classes) {
      if (entry.value.size > 1) {
        e = entry
        break
      }
    }

    if (e == null) {
      performEdits(data, result)
      return
    }

    val context = data.requireContext()
    DialogUtils.newMaterialDialogBuilder(context)
      .setCancelable(true)
      .setItems(e.value.toTypedArray()) { dialog, which ->
        dialog.dismiss()
        result.classes[e.key] = listOf(e.value[which])

        // once the user decides which class to import for this simple name,
        // call this method again to see if there any other simple names with multiple options
        finalizeClassNames(data, result)
      }
      .setTitle(context.getString(R.string.title_class_chooser, e.key))
      .show()
  }

  private fun performEdits(data: ActionData, result: Result) {
    val path = data.requirePath()
    val compiler = data.requireCompiler()
    val client =
      data.getLanguageClient()
        ?: run {
          log.warn("No language client found. Cannot perform edits.")
          return
        }

    val classes = result.classes.mapNotNull { it.value.firstOrNull() }

    if (classes.isEmpty()) {
      flashInfo(R.string.msg_no_unresolved_classes)
      return
    }

    val insertText = StringBuilder()
    if (result.fileImports.isEmpty() && classes.isNotEmpty()) {
      // if there are no file imports, the new imports will be added just after the package
      // declaration. To avoid this, add a new line before the imports
      insertText.append("\n")
    }

    for (klass in classes) {
      insertText.append("import ${klass};\n")
    }

    val position = compiler.compile(path).get { positionForImports(classes[0], it) }

    val change = DocumentChange()
    change.file = path
    change.edits = listOf(TextEdit(Range.pointRange(position), insertText.toString()))

    val action = CodeActionItem()
    action.title = data.requireContext().getString(R.string.title_fix_imports)
    action.kind = CodeActionKind.QuickFix
    action.changes = listOf(change)
    client.performCodeAction(action)
  }

  /**
   * Walks through the diagnostics of the compilation task, looks for [DiagnosticCode.NOT_IMPORTED]
   * errors and returns a list of simple names of all not imported classes.
   */
  private fun unresolvedNames(file: Path, task: CompileTask): List<String> {
    val names = mutableListOf<String>()
    var docContents: CharSequence? = null
    val diagnostics =
      task.diagnostics.filter {
        it.source.toUri() == file.toUri() && it.code == DiagnosticCode.NOT_IMPORTED.id
      }
    for (diagnostic in diagnostics) {
      val content =
        try {
          docContents ?: diagnostic.source.getCharContent(true).also { docContents = it }
        } catch (e: Exception) {
          log.error("Failed to get contents of file {}", file, e)
          continue
        }

      val name =
        content.subSequence(diagnostic.startPosition.toInt(), diagnostic.endPosition.toInt())
      names.add(name.toString())
    }
    return names
  }

  private fun getFileImports(task: CompileTask, file: Path): Set<String> {
    return task.root(file).imports.map { it.qualifiedIdentifier }.map { it.toString() }.toSet()
  }

  inner class Result(val fileImports: Set<String>, val classes: MutableMap<String, List<String>>)
}

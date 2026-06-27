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

import com.google.common.collect.Iterables.toArray
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.hasRequiredData
import com.neo.ide.actions.markInvisible
import com.neo.ide.actions.newDialogBuilder
import com.neo.ide.actions.requireFile
import com.neo.ide.actions.requirePath
import com.neo.ide.javac.services.util.JavaDiagnosticUtils
import com.neo.ide.lsp.java.JavaCompilerProvider
import com.neo.ide.lsp.java.actions.BaseJavaCodeAction
import com.neo.ide.lsp.java.models.DiagnosticCode
import com.neo.ide.lsp.java.rewrite.AddImport
import com.neo.ide.lsp.java.rewrite.Rewrite
import com.neo.ide.lsp.models.CodeActionItem
import com.neo.ide.lsp.models.DiagnosticItem
import com.neo.ide.projects.IProjectManager
import com.neo.ide.resources.R
import jdkx.tools.Diagnostic
import jdkx.tools.JavaFileObject
import org.slf4j.LoggerFactory

/** @author Akash Yadav */
class AddImportAction : BaseJavaCodeAction() {

  override val id: String = "ide.editor.lsp.java.diagnostics.addImport"
  override var label: String = ""
  private val diagnosticCode = DiagnosticCode.NOT_IMPORTED.id

  override val titleTextRes: Int = R.string.action_import_classes

  companion object {

    private val log = LoggerFactory.getLogger(AddImportAction::class.java)
  }

  override fun prepare(data: ActionData) {
    super.prepare(data)

    if (!visible || !data.hasRequiredData(DiagnosticItem::class.java)) {
      markInvisible()
      return
    }

    val diagnostic = data.get(DiagnosticItem::class.java)!!
    if (diagnosticCode != diagnostic.code || diagnostic.extra !is Diagnostic<*>) {
      markInvisible()
      return
    }

    val file = data.requireFile()
    val module =
      IProjectManager.getInstance().getWorkspace()?.findModuleForFile(file, false)
        ?: run {
          markInvisible()
          return
        }

    val compiler = JavaCompilerProvider.get(module)

    @Suppress("UNCHECKED_CAST")
    val jcDiagnostic =
      JavaDiagnosticUtils.asJCDiagnostic(diagnostic.extra as Diagnostic<out JavaFileObject>)
    if (jcDiagnostic == null) {
      markInvisible()
      return
    }

    val found =
      jcDiagnostic.args[1]?.toString()?.let { compiler.findQualifiedNames(it, true).isNotEmpty() }
        ?: false

    visible = found
    enabled = found
  }

  override suspend fun execAction(data: ActionData): Any {
    @Suppress("UNCHECKED_CAST")
    val diagnostic =
      JavaDiagnosticUtils.asUnwrapper(
        data.get(DiagnosticItem::class.java)!!.extra as Diagnostic<out JavaFileObject>
      )!!
    val file = data.requireFile()
    val module =
      IProjectManager.getInstance().getWorkspace()?.findModuleForFile(file, false)
        ?: run {
          markInvisible()
          return Any()
        }

    val compiler = JavaCompilerProvider.get(module)

    val titles = mutableListOf<String>()
    val rewrites = mutableListOf<AddImport>()
    val simpleName = diagnostic.d.args[1]
    for (name in compiler.publicTopLevelTypes()) {
      var klass = name
      if (klass.contains('/')) {
        klass = klass.replace('/', '.')
      }

      if (!klass.endsWith(".$simpleName")) {
        continue
      }

      titles.add(klass)
      rewrites.add(AddImport(data.requirePath(), klass))
    }

    if (rewrites.isEmpty()) {
      return false
    }

    return Pair(titles, rewrites)
  }

  @Suppress("UNCHECKED_CAST")
  override fun postExec(data: ActionData, result: Any) {

    if (result !is Pair<*, *>) {
      return
    }

    val file = data.requireFile()
    val module =
      IProjectManager.getInstance().getWorkspace()?.findModuleForFile(file, false)
        ?: run {
          markInvisible()
          return
        }

    val compiler = JavaCompilerProvider.get(module)
    val client = data.getLanguageClient() ?: return
    val actions = mutableListOf<CodeActionItem>()
    val titles = result.first as List<String>
    val rewrites = result.second as List<Rewrite>

    for (index in rewrites.indices) {
      val name = titles[index]
      val rewrite = rewrites[index]
      rewrite.asCodeActions(compiler, name)?.let { actions.add(it) }
    }

    when (actions.size) {
      0 -> {
        log.warn("No rewrites found. Cannot perform action")
      }

      1 -> {
        client.performCodeAction(actions[0])
      }

      else -> {
        val builder = newDialogBuilder(data)
        builder.setTitle(label)
        builder.setItems(toArray(titles, String::class.java)) { d, w ->
          d.dismiss()
          client.performCodeAction(actions[w])
        }
        builder.show()
      }
    }
  }
}

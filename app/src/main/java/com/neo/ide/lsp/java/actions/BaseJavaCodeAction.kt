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



package com.neo.ide.lsp.java.actions

import android.content.Context
import android.graphics.drawable.Drawable
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.ActionItem
import com.neo.ide.actions.EditorActionItem
import com.neo.ide.actions.hasRequiredData
import com.neo.ide.actions.markInvisible
import com.neo.ide.actions.requireFile
import com.neo.ide.lsp.api.ILanguageClient
import com.neo.ide.lsp.api.ILanguageServerRegistry
import com.neo.ide.lsp.java.JavaCompilerProvider
import com.neo.ide.lsp.java.JavaLanguageServer
import com.neo.ide.lsp.java.R
import com.neo.ide.lsp.java.compiler.JavaCompilerService
import com.neo.ide.lsp.java.rewrite.Rewrite
import com.neo.ide.projects.IProjectManager
import com.neo.ide.utils.DocumentUtils
import com.neo.ide.utils.ILogger
import com.neo.ide.utils.flashError
import java.io.File

/**
 * Base class for java code actions
 *
 * @author Akash Yadav
 */
abstract class BaseJavaCodeAction : EditorActionItem {

  override var visible: Boolean = true
  override var enabled: Boolean = true
  override var icon: Drawable? = null
  override var requiresUIThread: Boolean = false
  override var location: ActionItem.Location = ActionItem.Location.EDITOR_CODE_ACTIONS

  protected abstract val titleTextRes: Int

  override fun prepare(data: ActionData) {
    super.prepare(data)
    if (
      !data.hasRequiredData(Context::class.java, JavaLanguageServer::class.java, File::class.java)
    ) {
      markInvisible()
      return
    }

    if (titleTextRes != -1) {
      label = data[Context::class.java]!!.getString(titleTextRes)
    }

    val file = data.requireFile()
    visible = DocumentUtils.isJavaFile(file.toPath())
    enabled = visible
  }

  fun performCodeAction(data: ActionData, result: Rewrite) {
    val compiler = data.requireCompiler()

    val actions =
      try {
        result.asCodeActions(compiler, label)
      } catch (e: Exception) {
        flashError(e.cause?.message ?: e.message)
        ILogger.ROOT.error(e.cause?.message ?: e.message, e)
        return
      }

    if (actions == null) {
      onPerformCodeActionFailed(data)
      return
    }

    data.getLanguageClient()?.performCodeAction(actions)
  }

  protected open fun onPerformCodeActionFailed(data: ActionData) {
    flashError(R.string.msg_codeaction_failed)
  }

  protected fun ActionData.requireLanguageServer(): JavaLanguageServer {
    return ILanguageServerRegistry.getDefault().getServer(JavaLanguageServer.SERVER_ID)
        as JavaLanguageServer
  }

  protected fun ActionData.getLanguageClient(): ILanguageClient? {
    return requireLanguageServer().client
  }

  protected fun ActionData.requireCompiler(): JavaCompilerService {
    val module =
      IProjectManager.getInstance().getWorkspace()?.findModuleForFile(requireFile(), false)
    requireNotNull(module) {
      "Cannot get compiler instance. Unable to find module for file: ${requireFile().name}"
    }
    return JavaCompilerProvider.get(module)
  }
}

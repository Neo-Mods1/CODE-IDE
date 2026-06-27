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



package com.neo.ide.actions.etc

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.neo.ide.R
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.EditorActivityAction
import com.neo.ide.editor.schemes.IDEColorSchemeProvider
import com.neo.ide.tasks.launchAsyncWithProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Action which reloads the editor color schemes.
 *
 * @author Akash Yadav
 */
class ReloadColorSchemesAction(context: Context, override val order: Int) : EditorActivityAction() {

  override val id: String = "ide.editor.colorScheme.reload"

  // Schemes are reloaded in a background thread
  // This property is set to true just to make sure that the ProgressDialog instance is created on
  // the UI thread
  override var requiresUIThread: Boolean = true

  init {
    label = context.getString(R.string.title_reload_color_schemes)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_reload)
  }

  override suspend fun execAction(data: ActionData): Boolean {
    val context = data.requireActivity()
    context.lifecycleScope.launchAsyncWithProgress(
      context = Dispatchers.Default,
      configureFlashbar = { builder, _ ->
        builder.message(R.string.please_wait)
      }) { flashbar, _ ->
      IDEColorSchemeProvider.reload()
      withContext(Dispatchers.Main) {
        flashbar.dismiss()
      }
    }
    return true
  }
}

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



package com.neo.ide.actions.sidebar

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.neo.ide.R
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.requireContext
import com.neo.ide.activities.TerminalActivity
import com.neo.ide.projects.IProjectManager
import com.termux.shared.termux.TermuxConstants.TERMUX_APP.TERMUX_ACTIVITY
import java.util.Objects
import kotlin.reflect.KClass

/**
 * Sidebar action for opening the terminal.
 *
 * @author Akash Yadav
 */
class TerminalSidebarAction(context: Context, override val order: Int) : AbstractSidebarAction() {

  override val id: String = ID
  override val fragmentClass: KClass<out Fragment>? = null

  init {
    label = context.getString(R.string.title_terminal)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_terminal)
  }

  companion object {

    const val ID = "ide.editor.sidebar.terminal"

    fun startTerminalActivity(data: ActionData, isFailsafe: Boolean) {
      val context = data.requireContext()
      val intent = Intent(context, TerminalActivity::class.java).apply {
        putExtra(
          TERMUX_ACTIVITY.EXTRA_SESSION_WORKING_DIR,
          Objects.requireNonNull(IProjectManager.getInstance().projectDirPath)
        )
        putExtra(
          TERMUX_ACTIVITY.EXTRA_SESSION_NAME,
          IProjectManager.getInstance().getWorkspace()?.getRootProject()?.name
        )
        putExtra(TERMUX_ACTIVITY.EXTRA_FAILSAFE_SESSION, isFailsafe)
      }
      context.startActivity(intent)
    }
  }

  override suspend fun execAction(data: ActionData): Any {
    startTerminalActivity(data, false)
    return true
  }
}
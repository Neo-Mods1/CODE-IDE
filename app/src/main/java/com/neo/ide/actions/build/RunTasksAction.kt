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



package com.neo.ide.actions.build

import android.content.Context
import androidx.core.content.ContextCompat
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.BaseBuildAction
import com.neo.ide.fragments.RunTasksDialogFragment
import com.neo.ide.resources.R

/** @author Akash Yadav */
class RunTasksAction(context: Context, override val order: Int) : BaseBuildAction() {
  override val id: String = "ide.editor.build.runTasks"
  private var dialog: RunTasksDialogFragment? = null

  init {
    label = context.getString(R.string.title_run_tasks)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_run_tasks)
  }

  override suspend fun execAction(data: ActionData): Any {
    dialog?.dismiss()
    dialog = null
    dialog = RunTasksDialogFragment()
    return dialog!!
  }

  override fun postExec(data: ActionData, result: Any) {
    if (result !is RunTasksDialogFragment) {
      return
    }

    val activity = data.getActivity()!!
    result.show(activity.supportFragmentManager, this.id)
  }
  
  override fun destroy() {
    super.destroy()
    try {
      dialog?.dismiss()
    } catch (e: Exception) {
      // ignored
    }
    dialog = null
  }
}

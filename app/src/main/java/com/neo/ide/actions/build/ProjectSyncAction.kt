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
import com.neo.ide.resources.R
import com.neo.ide.resources.R.string

/**
 * Triggers a project sync request.
 *
 * @author Akash Yadav
 */
class ProjectSyncAction(context: Context, override val order: Int) : BaseBuildAction() {

  override val id: String = "ide.editor.syncProject"
  override var requiresUIThread = false

  init {
    label = context.getString(string.title_sync_project)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_sync)
  }

  override suspend fun execAction(data: ActionData): Any {
    return data.requireActivity().saveAll(requestSync = false)
  }

  override fun postExec(data: ActionData, result: Any) {
    val activity = data.requireActivity()
    activity.initializeProject()
  }
}

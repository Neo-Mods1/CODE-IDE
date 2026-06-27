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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.requireContext
import com.neo.ide.activities.editor.BaseEditorActivity
import com.neo.ide.resources.R
import kotlin.reflect.KClass

/**
 * Sidebar action for closing the project.
 *
 * @author Akash Yadav
 */
class CloseProjectSidebarAction(context: Context, override val order: Int) :
  AbstractSidebarAction() {

  override val id: String = "ide.editor.sidebar.closeProject"
  override val fragmentClass: KClass<out Fragment>? = null

  init {
    label = context.getString(R.string.title_close_project)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_folder_close)
  }

  override suspend fun execAction(data: ActionData): Any {
    val context = data.requireContext() as BaseEditorActivity
    context.doConfirmProjectClose()
    return true
  }
}
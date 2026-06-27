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
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.ActionItem
import com.neo.ide.actions.ActionMenu
import com.neo.ide.actions.EditorActivityAction
import com.neo.ide.resources.R

/** @author Akash Yadav */
class FindActionMenu(context: Context, override val order: Int) : EditorActivityAction(),
  ActionMenu {

  override val children: MutableSet<ActionItem> = mutableSetOf()
  override val id: String = "ide.editor.find"

  init {
    label = context.getString(R.string.menu_find)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_search)

    addAction(FindInFileAction(context, 0))
    addAction(FindInProjectAction(context, 1))
  }

  override fun prepare(data: ActionData) {
    super<EditorActivityAction>.prepare(data)
    super<ActionMenu>.prepare(data)
  }
}

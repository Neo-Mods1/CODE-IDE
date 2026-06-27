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
import com.neo.ide.actions.EditorRelatedAction
import com.neo.ide.resources.R

/** @author Akash Yadav */
class FindInFileAction() : EditorRelatedAction() {

  override val id: String = "ide.editor.find.inFile"
  override var requiresUIThread: Boolean = true

  override var order: Int = 0

  constructor(context: Context, order: Int) : this() {
    this.label = context.getString(R.string.menu_find_file)
    this.icon = ContextCompat.getDrawable(context, R.drawable.ic_search_file)
    this.order = order
  }

  override suspend fun execAction(data: ActionData): Boolean {
    val editor = data.getEditorView() ?: return false
    editor.beginSearch()
    return true
  }
}

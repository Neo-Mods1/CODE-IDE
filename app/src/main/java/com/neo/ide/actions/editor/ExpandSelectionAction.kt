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



package com.neo.ide.actions.editor

import android.content.Context
import androidx.core.content.ContextCompat
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.ActionItem
import com.neo.ide.actions.EditorRelatedAction
import com.neo.ide.resources.R

/**
 * Expands the current selection using the language server set in editor.
 *
 * @author Akash Yadav
 */
class ExpandSelectionAction(context: Context, override val order: Int) : EditorRelatedAction() {
  override val id: String = "ide.editor.code.actions.expandSelection"
  override var location: ActionItem.Location = ActionItem.Location.EDITOR_TEXT_ACTIONS

  init {
    label = context.getString(R.string.action_expand_selection)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_expand_selection)
  }

  override suspend fun execAction(data: ActionData): Any {
    return data.getEditor()?.expandSelection() ?: false
  }
}

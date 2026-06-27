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

/*
 *  This file is part of AndroidIDE.
 *  
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.neo.ide.actions.editor

import android.content.Context
import androidx.core.content.ContextCompat
import com.neo.ide.R
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.ActionItem
import com.neo.ide.actions.EditorRelatedAction
import com.neo.ide.actions.markInvisible

/**
 * An action to long select text in the editor.
 *
 * @author Akash Yadav
 */
class LongSelectAction(context: Context, override val order: Int) : EditorRelatedAction() {

  override val id: String = "ide.editor.code.text.longSelect"
  override var location: ActionItem.Location = ActionItem.Location.EDITOR_TEXT_ACTIONS

  init {
    label = context.getString(R.string.title_begin_long_select)
    icon = ContextCompat.getDrawable(context, R.drawable.editor_text_select_start)
  }

  override fun prepare(data: ActionData) {
    super.prepare(data)
    data.getEditor() ?: markInvisible()
  }

  override suspend fun execAction(data: ActionData): Any {
    return data.getEditor()?.beginLongSelect() ?: false
  }
}
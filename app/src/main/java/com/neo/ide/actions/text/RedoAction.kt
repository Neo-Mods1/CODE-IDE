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



package com.neo.ide.actions.text

import android.app.Activity
import android.content.Context
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.KeyboardUtils
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.EditorRelatedAction
import com.neo.ide.actions.markInvisible
import com.neo.ide.resources.R

/** @author Akash Yadav */
class RedoAction(context: Context, override val order: Int) : EditorRelatedAction() {

  init {
    label = context.getString(R.string.redo)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_redo)
  }

  override val id: String = "ide.editor.code.text.redo"

  override fun prepare(data: ActionData) {
    super.prepare(data)

    if (!visible) {
      return
    }

    val editor = data.getEditor() ?: run {
      markInvisible()
      return
    }

    enabled = editor.canRedo()
  }

  override suspend fun execAction(data: ActionData): Boolean {
    val editor = data.getEditor() ?: run {
      markInvisible()
      return false
    }

    editor.redo()
    data.getActivity()?.invalidateOptionsMenu()
    return true
  }

  override fun getShowAsActionFlags(data: ActionData): Int {
    return if (KeyboardUtils.isSoftInputVisible(data.get(Context::class.java) as Activity)) {
      MenuItem.SHOW_AS_ACTION_IF_ROOM
    } else {
      MenuItem.SHOW_AS_ACTION_NEVER
    }
  }
}

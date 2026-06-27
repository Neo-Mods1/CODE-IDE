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



package com.neo.ide.actions.file

import android.content.Context
import androidx.core.content.ContextCompat
import com.neo.ide.R
import com.neo.ide.actions.ActionData
import com.neo.ide.activities.editor.EditorHandlerActivity

/**
 * Closes the current file.
 *
 * @author Akash Yadav
 */
class CloseFileAction(context: Context, override val order: Int) : FileTabAction() {

  override val id: String = "ide.editor.fileTab.close.current"

  init {
    label = context.getString(R.string.action_closeThis)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_close_this)
  }

  override fun EditorHandlerActivity.doAction(data: ActionData): Boolean {
    content.tabs.selectedTabPosition.let { index ->
      closeFile(index) {
        invalidateOptionsMenu()
      }
    }
    return true
  }
}

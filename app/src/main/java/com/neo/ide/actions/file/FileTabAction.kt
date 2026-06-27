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

import com.neo.ide.actions.ActionData
import com.neo.ide.actions.ActionItem.Location
import com.neo.ide.actions.ActionItem.Location.EDITOR_FILE_TABS
import com.neo.ide.actions.EditorActivityAction
import com.neo.ide.actions.markInvisible
import com.neo.ide.activities.editor.EditorHandlerActivity

/**
 * Action related to file tabs. Shown only when there is at least one file opened.
 *
 * @author Akash Yadav
 */
abstract class FileTabAction : EditorActivityAction() {

  override var location: Location = EDITOR_FILE_TABS
  override var requiresUIThread: Boolean = true

  override fun prepare(data: ActionData) {
    super.prepare(data)

    if (!visible) {
      return
    }

    val activity =
      data.getActivity()
        ?: run {
          markInvisible()
          return
        }

    visible = activity.editorViewModel.getOpenedFiles().isNotEmpty()
    enabled = visible
  }

  override suspend fun execAction(data: ActionData): Any {
    val activity = data.getActivity() ?: return false
    return activity.doAction(data)
  }

  abstract fun EditorHandlerActivity.doAction(data: ActionData): Boolean
}

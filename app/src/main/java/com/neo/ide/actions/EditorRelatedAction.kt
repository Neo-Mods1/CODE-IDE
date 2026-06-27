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



package com.neo.ide.actions

import com.neo.ide.editor.ui.IDEEditor
import com.neo.ide.ui.CodeEditorView

/** @author Akash Yadav */
abstract class EditorRelatedAction : EditorActivityAction(), EditorActionItem {

  override var requiresUIThread: Boolean = true

  override fun prepare(data: ActionData) {
    super<EditorActionItem>.prepare(data)
    super<EditorActivityAction>.prepare(data)
    val editor =
      data.getEditor()
        ?: run {
          visible = false
          enabled = false
          return
        }

    val file = editor.file

    visible = file != null
    enabled = visible
  }

  fun ActionData.getEditor(): IDEEditor? = get(IDEEditor::class.java)

  fun ActionData.getEditorView(): CodeEditorView? = get(CodeEditorView::class.java)

}

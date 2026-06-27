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
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.BaseEditorAction

/** @author Akash Yadav */
class PasteAction(context: Context, override val order: Int) : BaseEditorAction() {

  init {
    label = context.getString(android.R.string.paste)

    val arr = context.obtainStyledAttributes(intArrayOf(android.R.attr.actionModePasteDrawable))
    icon = arr.getDrawable(0)?.let { tintDrawable(context, it) }
    arr.recycle()
  }

  override val id: String = "ide.editor.code.text.paste"

  override fun prepare(data: ActionData) {
    super.prepare(data)

    if (!visible) {
      return
    }

    visible = getEditor(data)?.isEditable ?: false
    enabled = visible
  }

  override suspend fun execAction(data: ActionData): Boolean {
    val editor = getEditor(data) ?: return false
    editor.pasteText()
    return true
  }
}

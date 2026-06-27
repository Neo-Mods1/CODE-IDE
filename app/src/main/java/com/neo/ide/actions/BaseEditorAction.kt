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

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.neo.ide.editor.ui.IDEEditor

/** @author Akash Yadav */
abstract class BaseEditorAction : EditorActionItem {

  override var label: String = ""
  override var visible: Boolean = true
  override var enabled: Boolean = true
  override var icon: Drawable? = null
  override var requiresUIThread: Boolean = true // all editor actions must be executed on UI thread
  override var location: ActionItem.Location = ActionItem.Location.EDITOR_TEXT_ACTIONS

  override fun prepare(data: ActionData) {
    super.prepare(data)
    getEditor(data)
      ?: kotlin.run {
        visible = false
        enabled = false
        return
      }

    visible = true
    enabled = true
  }

  fun getEditor(data: ActionData): IDEEditor? {
    return data.get(IDEEditor::class.java)
  }

  fun getContext(data: ActionData): Context? {
    val editor = getEditor(data) ?: return null
    return editor.context
  }

  fun tintDrawable(context: Context, drawable: Drawable): Drawable {
    drawable.setTint(
      ContextCompat.getColor(context, com.neo.ide.resources.R.color.primaryIconColor)
    )
    return drawable
  }
}

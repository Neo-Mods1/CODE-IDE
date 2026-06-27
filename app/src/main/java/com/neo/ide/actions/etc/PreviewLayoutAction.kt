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
import android.content.Intent
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.android.aaptcompiler.AaptResourceType.LAYOUT
import com.android.aaptcompiler.extractPathData
import com.blankj.utilcode.util.KeyboardUtils
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.EditorRelatedAction
import com.neo.ide.actions.markInvisible
import com.neo.ide.activities.editor.EditorHandlerActivity
import com.neo.ide.editor.ui.IDEEditor
import com.neo.ide.resources.R
import com.neo.ide.uidesigner.UIDesignerActivity
import java.io.File

/** @author Akash Yadav */
class PreviewLayoutAction(context: Context, override val order: Int) : EditorRelatedAction() {

  override val id: String = "ide.editor.previewLayout"

  override var requiresUIThread: Boolean = false

  init {
    label = context.getString(R.string.title_preview_layout)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_preview_layout)
  }

  override fun prepare(data: ActionData) {
    super.prepare(data)

    val viewModel = data.requireActivity().editorViewModel
    if (viewModel.isInitializing) {
      visible = true
      enabled = false
      return
    }

    if (!visible) {
      return
    }

    val editor = data.requireEditor()
    val file = editor.file!!

    val isXml = file.name.endsWith(".xml")

    if (!isXml) {
      markInvisible()
      return
    }

    val type = try {
      extractPathData(file).type
    } catch (err: Throwable) {
      markInvisible()
      return
    }

    visible = type == LAYOUT
    enabled = visible
  }

  override fun getShowAsActionFlags(data: ActionData): Int {
    val activity = data.getActivity() ?: return super.getShowAsActionFlags(data)
    return if (KeyboardUtils.isSoftInputVisible(activity)) {
      MenuItem.SHOW_AS_ACTION_IF_ROOM
    } else {
      MenuItem.SHOW_AS_ACTION_ALWAYS
    }
  }

  override suspend fun execAction(data: ActionData): Boolean {
    val activity = data.requireActivity()
    activity.saveAll()
    return true
  }

  override fun postExec(data: ActionData, result: Any) {
    val activity = data.requireActivity()
    activity.previewLayout(data.requireEditor().file!!)
  }

  private fun EditorHandlerActivity.previewLayout(file: File) {
    val intent = Intent(this, UIDesignerActivity::class.java)
    intent.putExtra(UIDesignerActivity.EXTRA_FILE, file.absolutePath)
    uiDesignerResultLauncher?.launch(intent)
  }

  private fun ActionData.requireEditor(): IDEEditor {
    return this.getEditor() ?: throw IllegalArgumentException(
      "An editor instance is required but none was provided")
  }
}

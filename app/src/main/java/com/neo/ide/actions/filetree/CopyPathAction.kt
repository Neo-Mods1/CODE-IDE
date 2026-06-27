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



package com.neo.ide.actions.filetree

import android.content.Context
import com.blankj.utilcode.util.ClipboardUtils
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.requireFile
import com.neo.ide.resources.R
import com.neo.ide.utils.flashSuccess

/**
 * Action to copy the absolute path of the selected file.
 *
 * @author Akash Yadav
 */
class CopyPathAction(context: Context, override val order: Int) :
  BaseFileTreeAction(context, labelRes = R.string.copy_path, iconRes = R.drawable.ic_copy) {

  override val id: String = "ide.editor.fileTree.copyPath"

  override suspend fun execAction(data: ActionData) {
    val file = data.requireFile()
    ClipboardUtils.copyText("[AndroidIDE] Copied File Path", file.absolutePath)
    flashSuccess(R.string.copied)
  }
}

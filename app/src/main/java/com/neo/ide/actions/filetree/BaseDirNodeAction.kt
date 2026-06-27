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
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.markInvisible
import com.neo.ide.actions.requireFile

/**
 * Base class for action items for directory nodes.
 *
 * @author Akash Yadav
 */
abstract class BaseDirNodeAction(context: Context,
  @StringRes labelRes: Int? = null,
  @DrawableRes iconRes: Int? = null) : BaseFileTreeAction(context, labelRes, iconRes) {

  override fun prepare(data: ActionData) {
    super.prepare(data)
    if (!data.hasFileTreeData()) {
      markInvisible()
      return
    }

    val file = data.requireFile()
    if (!file.isDirectory) {
      markInvisible()
      return
    }

    visible = true
    enabled = true
  }
}

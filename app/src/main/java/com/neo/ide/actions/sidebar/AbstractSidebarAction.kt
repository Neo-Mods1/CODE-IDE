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



package com.neo.ide.actions.sidebar

import android.graphics.drawable.Drawable
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.ActionItem
import com.neo.ide.actions.SidebarActionItem

/**
 * @author Akash Yadav
 */
abstract class AbstractSidebarAction : SidebarActionItem {

  // sidebar actions should always be executed on UI thread
  override var requiresUIThread = true
  override var visible = true
  override var enabled = true

  // should never change
  final override var location = ActionItem.Location.EDITOR_SIDEBAR

  // Subclasses should accept a Context in their constructor and initialize these values
  // when the object instance is initialized
  override var icon: Drawable? = null
  override var label: String = ""

  override suspend fun execAction(data: ActionData): Any {
    return false
  }
}
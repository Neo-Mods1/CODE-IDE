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
import com.neo.ide.activities.editor.EditorHandlerActivity
import com.neo.ide.tasks.cancelIfActive
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.plus

/** @author Akash Yadav */
abstract class EditorActivityAction : ActionItem {

  override var enabled: Boolean = true
  override var visible: Boolean = true
  override var icon: Drawable? = null
  override var label: String = ""
  override var location: ActionItem.Location = ActionItem.Location.EDITOR_TOOLBAR

  override var requiresUIThread: Boolean = false

  protected val actionScope = CoroutineScope(Dispatchers.Default) +
      CoroutineName("${javaClass.simpleName}Scope")

  override fun prepare(data: ActionData) {
    super.prepare(data)
    if (!data.hasRequiredData(Context::class.java)) {
      markInvisible()
    }
  }

  fun ActionData.getActivity(): EditorHandlerActivity? {
    return this[Context::class.java] as? EditorHandlerActivity
  }

  fun ActionData.requireActivity(): EditorHandlerActivity {
    return getActivity()!!
  }

  override fun destroy() {
    super.destroy()
    actionScope.cancelIfActive("Action is being destroyed")
  }
}

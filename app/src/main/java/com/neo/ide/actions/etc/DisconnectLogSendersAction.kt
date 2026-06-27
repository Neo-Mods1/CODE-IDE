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
import androidx.core.content.ContextCompat
import com.neo.ide.R
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.EditorActivityAction
import com.neo.ide.actions.markInvisible
import com.neo.ide.services.log.lookupLogService

/**
 * An action to disconnect all connected LogSenders at once.
 *
 * @author Akash Yadav
 */
class DisconnectLogSendersAction(context: Context, override val order: Int) : EditorActivityAction() {

  override val id: String = "ide.editor.service.logreceiver.disconnectSenders"

  init {
    label = context.getString(R.string.title_disconnect_log_senders)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_logs_disconnect)
  }

  override fun prepare(data: ActionData) {
    super.prepare(data)
    data.getActivity() ?: run {
      markInvisible()
      return
    }

    val receiverService = lookupLogService()
    if (receiverService == null) {
      markInvisible()
      return
    }
  }

  override suspend fun execAction(data: ActionData): Any {
    val receiverService = lookupLogService()
    receiverService?.disconnectAll()

    markInvisible()
    data.getActivity()?.invalidateOptionsMenu()
    return true
  }
}
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

package com.neo.ide.tooling.impl.util

import ch.qos.logback.core.status.Status
import ch.qos.logback.core.status.StatusListener
import ch.qos.logback.core.util.StatusPrinter
import com.neo.ide.tooling.api.messages.LogMessageParams
import com.neo.ide.tooling.impl.Main

/**
 * @author Akash Yadav
 */
class LogbackStatusListener : StatusListener {

  companion object {

    private fun levelChar(level: Int): Char {
      return when (level) {
        Status.ERROR -> 'E'
        Status.WARN -> 'W'
        Status.INFO -> 'I'
        else -> 'D'
      }
    }
  }

  override fun addStatusEvent(status: Status?) {
    status ?: return
    val sb = StringBuilder(256)
    val client = Main.client
    StatusPrinter.buildStr(sb, "", status)

    client?.logMessage(
      LogMessageParams(
        levelChar(status.level),
        status.origin.javaClass.simpleName,
        sb.toString()
      )
    )
  }
}

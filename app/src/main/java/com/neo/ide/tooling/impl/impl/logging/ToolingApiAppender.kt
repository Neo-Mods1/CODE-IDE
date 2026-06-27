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

package com.neo.ide.tooling.impl.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import com.neo.ide.tooling.api.messages.LogMessageParams
import com.neo.ide.tooling.impl.Main

/**
 * [AppenderBase] implementation which forwards all logs to the tooling API client.
 *
 * @author Akash Yadav
 */
class ToolingApiAppender : AppenderBase<ILoggingEvent>() {

  override fun append(eventObject: ILoggingEvent?) {
    if (eventObject == null || !isStarted) {
      return
    }

    Main.client?.logMessage(
      LogMessageParams(
        eventObject.level.levelStr[0],
        eventObject.loggerName,
        eventObject.formattedMessage
      )
    )
  }
}

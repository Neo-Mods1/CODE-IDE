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

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.Configurator
import ch.qos.logback.classic.spi.ConfiguratorRank
import ch.qos.logback.core.spi.ContextAwareBase
import com.google.auto.service.AutoService
import com.neo.ide.logging.JvmStdErrAppender
import com.neo.ide.logging.encoder.IDELogFormatEncoder

/**
 * Default logging configurator for the Tooling API Runtime.
 *
 * @author Akash Yadav
 */
@ConfiguratorRank(ConfiguratorRank.CUSTOM_TOP_PRIORITY)
@AutoService(Configurator::class)
@Suppress("UNUSED")
class ToolingLoggingConfigurator : ContextAwareBase(), Configurator {

  override fun configure(context: LoggerContext): Configurator.ExecutionStatus {
    addInfo("Setting up logging configuration for tooling API")

    val stdErrAppender = JvmStdErrAppender()
    stdErrAppender.encoder = IDELogFormatEncoder()
    stdErrAppender.start()

    val toolingApiAppender = ToolingApiAppender()
    toolingApiAppender.start()

    val rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME)
    rootLogger.addAppender(stdErrAppender)
    rootLogger.addAppender(toolingApiAppender)

    return Configurator.ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY
  }
}

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



package com.neo.ide.fragments.output

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import com.neo.ide.R
import com.neo.ide.logging.LifecycleAwareAppender
import org.slf4j.LoggerFactory

/**
 * Fragment to show IDE logs.
 * @author Akash Yadav
 */
class IDELogFragment : LogViewFragment() {

  private val lifecycleAwareAppender = LifecycleAwareAppender(Lifecycle.State.CREATED)

  override fun isSimpleFormattingEnabled() = true
  override fun getFilename() = "ide_logs"

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    emptyStateViewModel.emptyMessage.value = getString(R.string.msg_emptyview_idelogs)

    lifecycleAwareAppender.consumer = this::appendLine
    lifecycleAwareAppender.attachTo(viewLifecycleOwner)

    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger

    lifecycleAwareAppender.context = loggerContext
    lifecycleAwareAppender.start()

    rootLogger.addAppender(lifecycleAwareAppender)
  }

  override fun onDestroy() {
    super.onDestroy()
    lifecycleAwareAppender.stop()

    val logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    logger.detachAppender(lifecycleAwareAppender)
  }
}
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



package com.neo.ide.javac.services.compiler

import com.neo.ide.javac.services.CancelService
import com.neo.ide.utils.ILogger
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Cancel service implementation for the java compiler.
 * @author Akash Yadav
 */
class CancelServiceImpl : CancelService() {
  val cancelled = AtomicBoolean(false)

  /**
   * Sets the cancellation flag.
   *
   * @return `true` if compilation process was running and it was set to be cancelled, `false`
   * otherwise.
   */
  fun cancel(): Boolean {
    ILogger.ROOT.info("...requesting compilation cancellation")
    return !cancelled.getAndSet(true)
  }

  override fun isCanceled(): Boolean = cancelled.get()
}

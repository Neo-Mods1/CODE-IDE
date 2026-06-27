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



package com.neo.ide.services.builder

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import org.slf4j.LoggerFactory

/**
 * [ServiceConnection] for [GradleBuildService].
 *
 * @author Akash Yadav
 */
class GradleBuildServiceConnnection : ServiceConnection {

  internal var onConnected: ((GradleBuildService) -> Unit)? = null

  companion object {

    private val log = LoggerFactory.getLogger(GradleBuildServiceConnnection::class.java)
  }

  override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
    val serviceBinder = service as GradleServiceBinder
    onConnected?.invoke(serviceBinder.service!!)
  }

  override fun onServiceDisconnected(name: ComponentName?) {
    onConnected = null
    log.info("Disconnected from Gradle build service")
  }
}
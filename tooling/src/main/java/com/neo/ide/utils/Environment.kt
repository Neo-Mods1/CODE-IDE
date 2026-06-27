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

package com.neo.ide.utils

import java.io.File

object Environment {

  val HOME: File by lazy {
    File(System.getProperty("user.home") ?: "/data/data/com.termux/files/home")
  }

  val JAVA_HOME: File by lazy {
    File(System.getProperty("java.home") ?: "/usr")
  }

  val JAVA: File by lazy {
    JAVA_HOME.resolve("bin").resolve("java")
  }

  val TMP_DIR: File by lazy {
    File(HOME, ".ide/tmp").also { it.mkdirs() }
  }

  val TOOLING_API_JAR: File by lazy {
    File(HOME, ".ide/tooling-api.jar")
  }

  val AAPT2: File by lazy {
    File(HOME, ".ide/aapt2")
  }

  val INIT_SCRIPT: File by lazy {
    File(HOME, ".ide/init.gradle").also {
      if (!it.exists()) {
        it.parentFile?.mkdirs()
        it.writeText("")
      }
    }
  }

  @JvmStatic
  fun mkdirIfNotExits(dir: File) {
    if (!dir.exists()) {
      dir.mkdirs()
    }
  }
}

fun java.io.InputStream.transferToStream(output: java.io.OutputStream): Long {
    var transferred: Long = 0
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    var read: Int
    while (this.read(buffer, 0, DEFAULT_BUFFER_SIZE).also { read = it } >= 0) {
        output.write(buffer, 0, read)
        transferred += read.toLong()
    }
    return transferred
}

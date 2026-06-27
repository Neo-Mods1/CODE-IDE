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


package com.neo.ide.activities.editor

import android.os.Process
import com.neo.ide.utils.Environment
import com.neo.ide.utils.transferToStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Reads the logs from AndroidIDE and saves it to a file in the projects directory.
 *
 * @author Akash Yadav
 */
class IDELogcatReader {

  private var job: Job? = null
  private var shouldRun = false

  companion object {

    private val log = LoggerFactory.getLogger(IDELogcatReader::class.java)
  }

  /**
   * Start reading the logs.
   */
  fun start() {
    shouldRun = true

    check(job == null) {
      "Logcat reader is already running"
    }

    job = CoroutineScope(Dispatchers.IO).launch {
      run()
    }
  }

  /**
   * Stop the log reader.
   */
  fun stop() {
    shouldRun = false
    job?.cancel("User requested cancellation")
    job = null
  }

  private fun run() {
    val date = Date()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.US)
    val outputFile = File(Environment.ANDROIDIDE_HOME,
      "logs/AndroidIDE-LOG-${dateFormat.format(date)}.txt")

    log.debug("Creating output file: {}", outputFile)

    outputFile.parentFile!!.mkdirs()
    try {
      outputFile.createNewFile()
    } catch (e: Exception) {
      log.error("Failed to create output file for log", e)
      return
    }

    outputFile.outputStream().buffered().use { writer ->
      try {
        val process = ProcessBuilder(
          "logcat",
          "--pid=${Process.myPid()}",
          "-v",
          "threadtime"
        ).let { builder ->
          builder.redirectErrorStream(true)
          builder.start()
        }

        process.inputStream.transferToStream(writer)
        writer.flush()

        log.info("Process ended with exit code: {}", process.waitFor())
      } catch (err: Throwable) {
        log.error("Failed to read logs", err)
      }
    }
  }
}
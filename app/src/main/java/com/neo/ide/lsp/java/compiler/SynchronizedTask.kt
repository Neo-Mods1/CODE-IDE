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



package com.neo.ide.lsp.java.compiler

import com.neo.ide.lsp.java.CompilationCancellationException
import com.neo.ide.lsp.java.utils.CancelChecker.Companion.isCancelled
import org.slf4j.LoggerFactory
import java.util.concurrent.Semaphore

class SynchronizedTask {

  @Volatile
  @PublishedApi
  internal var isCompiling = false

  @PublishedApi
  internal val semaphore = Semaphore(1)

  @PublishedApi
  internal var task: CompileTask? = null
    private set

  companion object {

    @PublishedApi
    internal val log = LoggerFactory.getLogger(SynchronizedTask::class.java)
  }

  inline fun run(crossinline taskConsumer: (CompileTask) -> Unit) {
    try {
      semaphore.acquire()
    } catch (e: InterruptedException) {
      throw CompilationCancellationException(e)
    }
    try {
      taskConsumer(task!!)
    } catch (err: Throwable) {
      if (!isCancelled(err)) {
        log.error("An error occurred while working with compilation task", err)
      }
      throw err
    } finally {
      semaphore.release()
    }
  }

  inline fun <T : Any?> get(crossinline action: (CompileTask) -> T): T {
    try {
      semaphore.acquire()
    } catch (e: InterruptedException) {
      throw CompilationCancellationException(e)
    }
    return try {
      action(task!!)
    } catch (err: Throwable) {
      if (!isCancelled(err)) {
        log.error("An error occurred while working with compilation task", err)
      }
      throw err
    } finally {
      semaphore.release()
    }
  }

  fun post(action: Runnable) = post { action.run() }

  inline fun post(action: () -> Unit) {
    try {
      semaphore.acquire()
    } catch (e: InterruptedException) {
      throw CompilationCancellationException(e)
    }
    isCompiling = true
    try {
      if (task != null) {
        task!!.close()
      }
      action()
    } catch (err: Throwable) {
      if (!isCancelled(err)) {
        log.error("An error occurred", err)
      }
      throw err
    } finally {
      semaphore.release()
      isCompiling = false
    }
  }

  fun setTask(task: CompileTask?) {
    this.task = task
  }

  @get:Synchronized
  val isBusy: Boolean
    get() = isCompiling || semaphore.availablePermits() == 0

  /**
   * **FOR INTERNAL USE ONLY!**
   */
  fun logStats() {
    log.warn("[SynchronizedTask] isCompiling={} queuedLength={}", isCompiling,
      semaphore.queueLength)
  }
}
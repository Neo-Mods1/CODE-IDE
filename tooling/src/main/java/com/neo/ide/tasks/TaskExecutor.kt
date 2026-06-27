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

package com.neo.ide.tasks

import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

object TaskExecutor {

  private val log = LoggerFactory.getLogger(TaskExecutor::class.java)
  private val mainHandler = Handler(Looper.getMainLooper())

  @JvmOverloads
  @JvmStatic
  fun <R> executeAsync(
    callable: Callable<R>,
    callback: Callback<R>? = null
  ): CompletableFuture<R?> {
    return CompletableFuture.supplyAsync {
        try {
          return@supplyAsync callable.call()
        } catch (th: Throwable) {
          log.error("An error occurred while executing Callable in background thread.", th)
          return@supplyAsync null
        }
      }
      .whenComplete { result, _ -> mainHandler.post { callback?.complete(result) } }
  }

  @JvmOverloads
  @JvmStatic
  fun <R> executeAsyncProvideError(
    callable: Callable<R>,
    callback: CallbackWithError<R>? = null
  ): CompletableFuture<R?> {
    return CompletableFuture.supplyAsync {
        try {
          return@supplyAsync callable.call()
        } catch (th: Throwable) {
          log.error("An error occurred while executing Callable in background thread.", th)
          throw CompletionException(th)
        }
      }
      .whenComplete { result, throwable ->
        mainHandler.post { callback?.complete(result, throwable) }
      }
  }

  fun interface Callback<R> {
    fun complete(result: R?)
  }

  fun interface CallbackWithError<R> {
    fun complete(result: R?, error: Throwable?)
  }
}

fun <R : Any?> executeAsync(callable: () -> R?) {
  executeAsync(callable) {}
}

@JvmOverloads
@Suppress("DEPRECATION")
inline fun <T> Context.executeWithProgress(
  cancellable: Boolean = false,
  block: (ProgressDialog) -> T
): T {
  val dialog = ProgressDialog(this)
  dialog.setMessage("Please wait...")
  dialog.setCancelable(cancellable)
  dialog.show()
  return block(dialog)
}

fun <R : Any?> executeAsync(callable: () -> R?, callback: (R?) -> Unit): CompletableFuture<R?> =
  TaskExecutor.executeAsync({ callable() }) { callback(it) }

fun <R : Any?> executeAsyncProvideError(
  callable: () -> R?,
  callback: (R?, Throwable?) -> Unit
): CompletableFuture<R?> =
  TaskExecutor.executeAsyncProvideError(callable, callback)

fun runOnUiThread(action: () -> Unit) {
  mainHandler.post(action)
}

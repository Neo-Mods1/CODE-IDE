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



package com.neo.ide.lsp.internal.model

import com.neo.ide.lsp.models.CompletionParams
import com.neo.ide.progress.ICancelChecker
import com.neo.ide.utils.DocumentUtils
import org.slf4j.LoggerFactory
import java.nio.file.Paths

/**
 * Cached version of a completion result. Keeps only required properties from [params].
 *
 * @author Akash Yadav
 */
class CachedCompletion
private constructor(
  val params: CompletionParams,
  val result: com.neo.ide.lsp.models.CompletionResult
) {

  companion object {

    private val log = LoggerFactory.getLogger(CachedCompletion::class.java)

    /** Empty cached completion. Could be used to represent "no cache available". */
    @JvmField
    val EMPTY =
      cache(
        CompletionParams(
          com.neo.ide.models.Position.NONE,
          Paths.get(""), ICancelChecker.CANCELLED
        ),
        com.neo.ide.lsp.models.CompletionResult.EMPTY
      )

    /**
     * Creates cached version of the result from the given params and result.
     *
     * @param _params The [CompletionParams] used to trigger the completion request. A shallow copy
     * of this request is created.
     * @param result The result of the completion to cache.
     */
    @JvmStatic
    fun cache(
      _params: CompletionParams,
      result: com.neo.ide.lsp.models.CompletionResult
    ): CachedCompletion {
      val params =
        CompletionParams(_params.position, _params.file, ICancelChecker.CANCELLED).apply {
          prefix = _params.prefix ?: ""
          content = ""
        }

      return CachedCompletion(params, result)
    }
  }

  fun canUseCache(params: CompletionParams): Boolean {
    val partial = params.requirePrefix()
    val position = this.params.position
    val file = this.params.file
    val prefix = this.params.requirePrefix()

    // The change in the length of the prefix
    val deltaPrefix = prefix.length - params.prefix!!.length

    // The change in the column index
    val deltaColumn = position.column - params.position.column

    // The changes must be of same length
    if (deltaPrefix != deltaColumn) {
      log.info("...unequal change in prefix and column")
      return false
    }

    if (position.line == -1 || position.column == -1) {
      log.info("...invalid cached completion position")
      return false
    }

    if (position.line != params.position.line || position.column > params.position.column) {
      log.info("...cursor line changed")
      return false
    }

    if (!DocumentUtils.isSameFile(file, params.file)) {
      log.info("...no cache available for current file")
      return false
    }

    if (!partial.startsWith(prefix) || partial.endsWith(".")) {
      log.info("...incompatible partial identifier")
      return false
    }

    return true
  }
}

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


package com.neo.ide.editor.language

import com.neo.ide.editor.language.utils.CompletionHelper
import com.neo.ide.lsp.api.ILanguageServer
import com.neo.ide.lsp.models.CompletionItem
import com.neo.ide.lsp.models.CompletionParams
import com.neo.ide.lsp.models.CompletionResult
import com.neo.ide.lsp.models.FailureType.COMPLETION
import com.neo.ide.lsp.models.LSPFailure
import com.neo.ide.lsp.util.setupLookupForCompletion
import com.neo.ide.models.Position
import io.github.rosemoe.sora.lang.completion.CompletionCancelledException
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.ContentReference
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.concurrent.CancellationException

/**
 * Common implementation of completion provider which requests completions to provided language
 * server.
 *
 * @author Akash Yadav
 */
internal class CommonCompletionProvider(
  private val server: ILanguageServer,
  private val cancelChecker: CompletionCancelChecker
) {

  companion object {

    private val log = LoggerFactory.getLogger(CommonCompletionProvider::class.java)
  }

  /**
   * Computes completion items using the provided language server instance.
   *
   * @param content The reference to the content of the editor.
   * @param file The file to compute completions for.
   * @param position The position of the cursor in the content.
   * @return The computed completion items. May return an empty list if the there was an error
   * computing the completion items.
   */
  inline fun complete(
    content: ContentReference,
    file: Path,
    position: CharPosition,
    prefixMatcher: (Char) -> Boolean
  ): List<CompletionItem> {
    val completionResult =
      try {
        setupLookupForCompletion(file)
        val prefix = CompletionHelper.computePrefix(content, position, prefixMatcher)
        val params =
          CompletionParams(Position(position.line, position.column, position.index), file,
            cancelChecker)
        params.content = content
        params.prefix = prefix
        server.complete(params)
      } catch (e: Throwable) {

        if (e is CancellationException) {
          log.debug("Completion process cancelled")
        }

        // Do not log if completion was interrupted or cancelled
        if (!(e is CancellationException || e is CompletionCancelledException)) {
          if (!server.handleFailure(LSPFailure(COMPLETION, e))) {
            log.error("Unable to compute completions", e)
          }
        }
        CompletionResult.EMPTY
      }

    if (completionResult == CompletionResult.EMPTY) {
      return listOf()
    }

    return completionResult.items
  }
}

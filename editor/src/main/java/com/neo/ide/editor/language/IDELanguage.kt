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

import android.os.Bundle
import com.neo.ide.editor.api.IEditor
import com.neo.ide.editor.ui.IDECompletionPublisher
import com.neo.ide.lookup.Lookup
import com.neo.ide.lsp.api.ILanguageServer
import com.neo.ide.preferences.internal.EditorPreferences
import com.neo.ide.progress.ICancelChecker
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.lang.completion.CompletionCancelledException
import io.github.rosemoe.sora.lang.completion.CompletionPublisher
import io.github.rosemoe.sora.lang.format.Formatter
import io.github.rosemoe.sora.text.CharPosition
import io.github.rosemoe.sora.text.ContentReference
import org.slf4j.LoggerFactory
import java.nio.file.Paths

/**
 * Base class for language implementations in the IDE.
 *
 * @author Akash Yadav
 */
abstract class IDELanguage : Language {

  private var formatter: Formatter? = null

  protected open val languageServer: ILanguageServer?
    get() = null

  open fun getTabSize(): Int {
    return EditorPreferences.tabSize
  }

  @Throws(CompletionCancelledException::class)
  override fun requireAutoComplete(
    content: ContentReference,
    position: CharPosition,
    publisher: CompletionPublisher,
    extraArguments: Bundle
  ) {
    try {
      val cancelChecker = CompletionCancelChecker(publisher)
      Lookup.getDefault().register(ICancelChecker::class.java, cancelChecker)
      doComplete(content, position, publisher, cancelChecker, extraArguments)
    } finally {
      Lookup.getDefault().unregister(
        ICancelChecker::class.java)
    }
  }

  private fun doComplete(
    content: ContentReference,
    position: CharPosition,
    publisher: CompletionPublisher,
    cancelChecker: CompletionCancelChecker,
    extraArguments: Bundle
  ) {
    val server = languageServer ?: return
    val path = extraArguments.getString(IEditor.KEY_FILE, null)
    if (path == null) {
      log.warn("Cannot provide completions. No file provided.")
      return
    }

    val completionProvider = CommonCompletionProvider(server, cancelChecker)
    val file = Paths.get(path)
    val completionItems = completionProvider.complete(content, file,
      position) { checkIsCompletionChar(it) }
    publisher.setUpdateThreshold(1)
    (publisher as IDECompletionPublisher).addLSPItems(completionItems)
  }

  /**
   * Check if the given character is a completion character.
   *
   * @param c The character to check.
   * @return `true` if the character is completion char, `false` otherwise.
   */
  protected open fun checkIsCompletionChar(c: Char): Boolean {
    return false
  }

  override fun useTab(): Boolean {
    return !EditorPreferences.useSoftTab
  }

  override fun getFormatter(): Formatter {
    return formatter ?: LSPFormatter(languageServer).also { formatter = it }
  }

  override fun getIndentAdvance(
    content: ContentReference,
    line: Int,
    column: Int
  ): Int {
    return getIndentAdvance(content.getLine(line).substring(0, column))
  }

  open fun getIndentAdvance(line: String): Int {
    return 0
  }

  companion object {

    private val log = LoggerFactory.getLogger(IDELanguage::class.java)
  }
}
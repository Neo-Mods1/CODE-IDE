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



package com.neo.ide.lsp.edits

import android.os.Looper
import com.blankj.utilcode.util.ThreadUtils
import com.neo.ide.lsp.models.Command
import com.neo.ide.lsp.models.CompletionItem
import com.neo.ide.lsp.models.InsertTextFormat.SNIPPET
import com.neo.ide.lsp.util.RewriteHelper
import io.github.rosemoe.sora.lang.completion.snippet.parser.CodeSnippetParser
import io.github.rosemoe.sora.text.Content
import io.github.rosemoe.sora.widget.CodeEditor
import org.slf4j.LoggerFactory

/**
 * Default edit handler for completion items.
 *
 * @author Akash Yadav
 */
open class DefaultEditHandler : IEditHandler {

  companion object {

    private val log = LoggerFactory.getLogger(DefaultEditHandler::class.java)
  }

  override fun performEdits(
    item: CompletionItem,
    editor: CodeEditor,
    text: Content,
    line: Int,
    column: Int,
    index: Int
  ) {
    if (Looper.myLooper() != Looper.getMainLooper()) {
      ThreadUtils.runOnUiThread { performEditsInternal(item, editor, text, line, column, index) }
      return
    }

    performEditsInternal(item, editor, text, line, column, index)
  }

  protected open fun performEditsInternal(
    item: CompletionItem,
    editor: CodeEditor,
    text: Content,
    line: Int,
    column: Int,
    index: Int
  ) {
    if (item.insertTextFormat == SNIPPET) {
      insertSnippet(item, editor, text, line, column, index)
      return
    }

    val start = getIdentifierStart(text.getLine(line), column)
    text.delete(line, start, line, column)
    editor.commitText(item.insertText)

    text.beginBatchEdit()
    if (item.additionalEditHandler != null) {
      item.additionalEditHandler!!.performEdits(item, editor, text, line, column, index)
    } else if (item.additionalTextEdits != null && item.additionalTextEdits!!.isNotEmpty()) {
      RewriteHelper.performEdits(item.additionalTextEdits!!, editor)
    }
    text.beginBatchEdit()

    executeCommand(editor, item.command)
  }

  protected open fun insertSnippet(
    item: CompletionItem,
    editor: CodeEditor,
    text: Content,
    line: Int,
    column: Int,
    index: Int
  ) {
    val snippetDescription = item.snippetDescription!!
    val snippet = CodeSnippetParser.parse(item.insertText)
    val prefixLength = snippetDescription.selectedLength
    val selectedText = text.subSequence(index - prefixLength, index).toString()
    var actionIndex = index
    if (snippetDescription.deleteSelected) {
      text.delete(index - prefixLength, index)
      actionIndex -= prefixLength
    }
    editor.snippetController.startSnippet(actionIndex, snippet, selectedText)

    if (snippetDescription.allowCommandExecution) {
      executeCommand(editor, item.command)
    }
  }

  protected open fun executeCommand(editor: CodeEditor, command: Command?) {
    if (command == null) {
      return
    }

    try {
      val klass = editor::class.java
      val method = klass.getMethod("executeCommand", Command::class.java)
      method.isAccessible = true
      method.invoke(editor, command)
    } catch (th: Throwable) {
      log.error("Unable to invoke 'executeCommand(Command) method in IDEEditor.", th)
    }
  }

  protected open fun getIdentifierStart(text: CharSequence, end: Int): Int {
    var start = end
    while (start > 0) {
      if (isPartialPart(text[start - 1])) {
        start--
        continue
      }
      break
    }
    return start
  }

  protected open fun isPartialPart(c: Char) = Character.isJavaIdentifierPart(c)
}

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



package com.neo.ide.editor.ui

import android.content.Intent
import android.net.Uri
import android.widget.ListView
import com.neo.ide.lsp.util.DocumentationReferenceProvider
import com.neo.ide.progress.ProgressManager
import com.neo.ide.utils.KeyboardUtils
import io.github.rosemoe.sora.lang.completion.CompletionItem
import io.github.rosemoe.sora.widget.component.CompletionLayout
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import org.slf4j.LoggerFactory
import java.lang.ref.WeakReference
import kotlin.math.min

/**
 * Completion window for the editor.
 *
 * @author Akash Yadav
 */
class EditorCompletionWindow(val editor: IDEEditor) : EditorAutoCompletion(editor) {

  private var listView: ListView? = null
  private val items: MutableList<CompletionItem> = mutableListOf()

  companion object {

    private val log = LoggerFactory.getLogger(EditorCompletionWindow::class.java)
  }

  init {
    setLayout(EditorCompletionLayout())
    setEnabledAnimation(true)
  }

  override fun isShowing(): Boolean {
    @Suppress("UNNECESSARY_SAFE_CALL", "USELESS_ELVIS")
    return popup?.isShowing ?: false
  }

  override fun setLayout(layout: CompletionLayout) {
    super.setLayout(layout)
    (layout.completionList as? ListView)?.let {
      listView = it
      it.adapter = this.adapter
      it.setOnItemLongClickListener { _, view, position, _ ->
        val data =
          (items[position] as? com.neo.ide.lsp.models.CompletionItem)?.data
            ?: return@setOnItemLongClickListener false
        val url =
          DocumentationReferenceProvider.getUrl(data) ?: return@setOnItemLongClickListener false
        Intent().apply {
          action = Intent.ACTION_VIEW
          setData(Uri.parse(url))
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          view.context.startActivity(this)
        }
        true
      }
    }
  }

  override fun select(pos: Int): Boolean {
    if (pos > adapter!!.count) {
      return false
    }
    return try {
      super.select(pos)
    } catch (e: Throwable) {
      log.warn("Unable to select completion item at {}", pos, e)
      false
    }
  }

  override fun select(): Boolean {
    return try {
      super.select()
    } catch (e: Throwable) {
      log.warn("Unable to select completion item", e)
      false
    }
  }

  override fun cancelCompletion() {
    if (completionThread != null) {
      ProgressManager.instance.cancel(completionThread)
    }
    super.cancelCompletion()
  }

  override fun requireCompletion() {
    if (cancelShowUp || !isEnabled || !editor.isAttachedToWindow) {
      return
    }

    val text = editor.text
    if (text.cursor.isSelected || checkNoCompletion()) {
      hide()
      return
    }

    if (System.nanoTime() - requestTime < editor.props.cancelCompletionNs) {
      hide()
      requestTime = System.nanoTime()
      return
    }

    cancelCompletion()
    requestTime = System.nanoTime()
    currentSelection = -1

    publisher =
      IDECompletionPublisher(
        editor.handler,
        {
          val items = publisher.items

          this.items.apply {
            clear()
            addAll(items)
          }

          if (lastAttachedItems == null || lastAttachedItems.get() != items) {
            adapter.attachValues(this, items)
            adapter.notifyDataSetInvalidated()
            lastAttachedItems = WeakReference(items)
          } else {
            adapter.notifyDataSetChanged()
          }

          val newHeight = (adapter!!.itemHeight * adapter!!.count).toFloat()
          if (newHeight == 0F) {
            hide()
          }

          editor.getComponent(EditorAutoCompletion::class.java).updateCompletionWindowPosition()
          setSize(width, min(newHeight, maxHeight.toFloat()).toInt())
          if (!isShowing) {
            show()
          }

          if (adapter!!.count >= 1
            && KeyboardUtils.isHardKeyboardConnected(context)
          ) {
            currentSelection = 0
          }
        },
        editor.editorLanguage.interruptionLevel
      )

    publisher.setUpdateThreshold(1)

    completionThread = CompletionThread(requestTime, publisher)
    completionThread.name = "CompletionThread-$requestTime"

    setLoading(true)

    completionThread.start()
  }

}

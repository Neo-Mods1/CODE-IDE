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

import com.neo.ide.eventbus.events.editor.DocumentChangeEvent
import com.neo.ide.eventbus.events.editor.DocumentCloseEvent
import com.neo.ide.eventbus.events.editor.DocumentEvent
import com.neo.ide.eventbus.events.editor.DocumentOpenEvent
import com.neo.ide.eventbus.events.editor.DocumentSaveEvent
import com.neo.ide.eventbus.events.editor.DocumentSelectedEvent
import com.neo.ide.projects.FileManager.onDocumentClose
import com.neo.ide.projects.FileManager.onDocumentContentChange
import com.neo.ide.projects.FileManager.onDocumentOpen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.slf4j.LoggerFactory
import java.util.concurrent.CancellationException
import java.util.concurrent.LinkedBlockingQueue

/**
 * Dispatches events for the editor.
 *
 * @author Akash Yadav
 */
class EditorEventDispatcher(
  var editor: IDEEditor? = null
) {

  private val eventQueue = LinkedBlockingQueue<DocumentEvent>()
  private var eventDispatcherJob: Job? = null

  companion object {

    private val log = LoggerFactory.getLogger(EditorEventDispatcher::class.java)
  }

  fun init(scope: CoroutineScope) {
    eventDispatcherJob = scope.launch(Dispatchers.Default) {
      while (isActive) {
        dispatchNextEvent()
      }
    }.also {
      it.invokeOnCompletion { error ->
        if (error != null && error !is CancellationException) {
          log.error("Failed to dispatch editor events", error)
        }
      }
    }
  }

  fun dispatch(event: DocumentEvent) {
    check(eventQueue.offer(event)) {
      "Failed to dispatch event: $event"
    }
  }

  private suspend fun dispatchNextEvent() {
    val event = withContext(Dispatchers.IO) {
      eventQueue.take()
    }

    if (editor?.isReleased != false) {
      return
    }

    when (event) {
      is DocumentOpenEvent -> dispatchOpen(event)
      is DocumentChangeEvent -> dispatchChange(event)
      is DocumentSaveEvent -> dispatchSave(event)
      is DocumentCloseEvent -> dispatchClose(event)
      is DocumentSelectedEvent -> dispatchSelected(event)
      else -> throw IllegalArgumentException("Unknown document event: $event")
    }
  }

  private fun dispatchOpen(event: DocumentOpenEvent) {
    onDocumentOpen(event)
    post(event)
  }

  private fun dispatchChange(event: DocumentChangeEvent) {
    onDocumentContentChange(event)
    post(event)
  }

  private fun dispatchSave(event: DocumentSaveEvent) {
    post(event)
  }

  private fun dispatchClose(event: DocumentCloseEvent) {
    onDocumentClose(event)
    post(event)
  }

  private fun dispatchSelected(event: DocumentSelectedEvent) {
    post(event)
  }

  private fun post(event: DocumentEvent) {
    EventBus.getDefault().post(event)
  }

  fun destroy() {
    editor = null
    eventDispatcherJob?.cancel(CancellationException("Cancellation requested"))
  }
}
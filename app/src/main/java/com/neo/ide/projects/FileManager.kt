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

package com.neo.ide.projects

import com.neo.ide.eventbus.events.editor.DocumentChangeEvent
import com.neo.ide.eventbus.events.editor.DocumentCloseEvent
import com.neo.ide.eventbus.events.editor.DocumentOpenEvent
import com.neo.ide.eventbus.events.file.FileDeletionEvent
import com.neo.ide.eventbus.events.file.FileRenameEvent
import com.neo.ide.progress.ProgressManager
import com.neo.ide.projects.models.ActiveDocument
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStream
import java.net.URI
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages active documents.
 *
 * @author Akash Yadav
 */
object FileManager {

  private val log = LoggerFactory.getLogger(FileManager::class.java)
  private val activeDocuments = ConcurrentHashMap<Path, ActiveDocument>()

  fun isActive(uri: URI): Boolean {
    return isActive(Paths.get(uri))
  }

  fun isActive(file: Path): Boolean {
    return this.activeDocuments.containsKey(file.normalize())
  }

  fun getActiveDocument(file: Path): ActiveDocument? {
    return this.activeDocuments[file.normalize()]
  }

  fun getActiveDocumentCount(): Int {
    return this.activeDocuments.size
  }

  fun getDocumentContents(file: Path): String {
    val document = getActiveDocument(file)
    if (document != null) {
      return document.content
    }

    return getFileContents(file)
  }

  fun getLastModified(file: Path): Instant {
    val document = getActiveDocument(file)
    if (document != null) {
      return document.modified
    }

    return getLastModifiedFromDisk(file)
  }

  fun getReader(file: Path): BufferedReader {
    val document = getActiveDocument(file)
    if (document != null) {
      return document.reader()
    }

    return createFileReader(file)
  }

  fun getInputStream(file: Path): InputStream {
    val document = getActiveDocument(file)
    if (document != null) {
      return document.inputStream()
    }

    return createFileInputStream(file)
  }

  fun onDocumentOpen(event: DocumentOpenEvent) {
    activeDocuments[event.openedFile.normalize()] = createDocument(event)
  }

  fun onDocumentContentChange(event: DocumentChangeEvent) {
    val document = activeDocuments[event.changedFile.normalize()]

    if (document == null) {
      // create document if not already created
      // this should not happen under normal circumstances
      activeDocuments[event.changedFile.normalize()] = createDocument(event)
      log.warn("Document change event received before open event for file {}", event.changedFile)
      return
    }

    document.version = event.version
    document.modified = Instant.now()
    document.content = event.newText!!
    event.newText = null
  }

  fun onDocumentClose(event: DocumentCloseEvent) {
    activeDocuments.remove(event.closedFile.normalize())
  }

  fun onFileRenamed(event: FileRenameEvent) {
    val document = activeDocuments.remove(event.file.toPath().normalize())
    if (document != null) {
      activeDocuments[event.newFile.toPath().normalize()] = document
    }
  }

  fun onFileDeleted(event: FileDeletionEvent) {
    // If the file was an active document, remove the document cache
    activeDocuments.remove(event.file.toPath().normalize())
  }

  private fun createDocument(event: DocumentOpenEvent): ActiveDocument {
    return ActiveDocument(
      file = event.openedFile,
      version = event.version,
      modified = Instant.now(),
      content = event.text
    )
  }

  private fun createDocument(event: DocumentChangeEvent): ActiveDocument {
    return ActiveDocument(
      file = event.changedFile,
      version = event.version,
      modified = Instant.now(),
      content = event.changedText
    )
  }

  private fun createFileReader(file: Path): BufferedReader {
    return try {
      Files.newBufferedReader(file)
    } catch (noFile: java.nio.file.NoSuchFileException) {
      log.warn("No such file", noFile)
      "".reader().buffered()
    } catch (cancelled: CancellationException) {
      "".reader().buffered()
    }
  }

  private fun createFileInputStream(file: Path): InputStream {
    return try {
      Files.newInputStream(file)
    } catch (noFile: java.nio.file.NoSuchFileException) {
      log.warn("No such file", noFile)
      "".byteInputStream()
    } catch (cancelled: CancellationException) {
      "".byteInputStream()
    }
  }

  private fun getLastModifiedFromDisk(file: Path): Instant {
    return Files.getLastModifiedTime(file).toInstant()
  }

  private fun getFileContents(file: Path): String {
    return try {
      ProgressManager.abortIfCancelled()
      FileUtils.readFileToString(file.toFile(), Charset.defaultCharset())
    } catch (noFile: java.nio.file.NoSuchFileException) {
      log.warn("No such file", noFile)
      ""
    } catch (cancelled: CancellationException) {
      ""
    }
  }
}

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



package com.neo.ide.lsp.java.parser.ts

import com.neo.ide.eventbus.events.file.FileDeletionEvent
import com.neo.ide.eventbus.events.file.FileRenameEvent
import com.neo.ide.lsp.java.parser.IJavaParser
import com.neo.ide.treesitter.TSParser
import com.neo.ide.treesitter.java.TSLanguageJava
import com.neo.ide.utils.StopWatch
import jdkx.tools.JavaFileObject
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.slf4j.LoggerFactory

/**
 * [IJavaParser] which uses tree sitter to parse source files.
 *
 * @author Akash Yadav
 */
object TSJavaParser : IJavaParser<TSParseResult> {

  private val cache = TSParseCache(15) // cache 15 results at max

  private var isClosed = false
  private val parser = TSParser.create().also { it.language = TSLanguageJava.getInstance() }
    get() {
      check(!isClosed) { "${javaClass.simpleName} instance has been closed" }
      return field
    }

  private val log = LoggerFactory.getLogger(TSJavaParser::class.java)

  init {
    EventBus.getDefault().register(this)
  }

  @Subscribe(threadMode = ThreadMode.ASYNC)
  fun onFileDeleted(event: FileDeletionEvent) {
    synchronized(this.cache) { this.cache.remove(event.file.toPath().toAbsolutePath().toUri()) }
  }

  @Subscribe(threadMode = ThreadMode.ASYNC)
  fun onFileRenamed(event: FileRenameEvent) {
    synchronized(this.cache) {
      val existing = this.cache.remove(event.file.toPath().toAbsolutePath().toUri())
      if (existing != null) {
        this.cache.put(event.newFile.toPath().toAbsolutePath().toUri(), existing)
      }
    }
  }

  override fun parse(file: JavaFileObject): TSParseResult {
    check(file.kind == JavaFileObject.Kind.SOURCE) { "File must a source file object" }

    synchronized(this.cache) {
      val result = this.cache[file.toUri()]
      if (result != null) {
        if (result.fileModified == file.lastModified) {
          // cache hit and cache modified == file modified
          log.info("Using cached parse tree")
          return result
        }
        // cache hit, but cache modified != file modified
        // need to reparse
      }
    }

    parser.reset()
    val watch = StopWatch("[TreeSitter] Parsing")
    val content = file.getCharContent(false).toString()
    if (parser.isParsing) {
      parser.requestCancellationAndWait()
    }
    val parseTree = parser.parseString(content)
    watch.log()

    val result = TSParseResult(file, parseTree)

    synchronized(this.cache) { this.cache.put(result.uri, result) }

    return result
  }

  override fun close() {
    synchronized(this.cache) { this.cache.evictAll() }
    parser.close()
    EventBus.getDefault().unregister(this)
    isClosed = true
  }
}

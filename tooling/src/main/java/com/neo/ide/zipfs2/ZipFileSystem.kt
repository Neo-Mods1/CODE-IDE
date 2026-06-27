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

package com.neo.ide.zipfs2

import java.io.File
import java.net.URI
import java.nio.file.attribute.FileAttribute
import java.nio.file.spi.FileSystemProvider

open class ZipFileSystem(
  provider: FileSystemProvider?,
  private val zipPath: URI,
  env: MutableMap<String, *> = mutableMapOf()
) : java.nio.file.FileSystem() {

  override fun provider(): FileSystemProvider = provider ?: ZipFileSystemProvider()

  override fun isOpen(): Boolean = true

  override fun isReadOnly(): Boolean = true

  override fun getSeparator(): String = "/"

  override fun getRootDirectories(): Iterable<Path> = emptyList()

  override fun getFileStores(): Iterable<java.nio.file.FileStore> = emptyList()

  override fun supportedFileAttributeViews(): MutableSet<String> = mutableSetOf("basic")

  override fun getPath(first: String, vararg more: String): java.nio.file.Path {
    val path = if (more.isEmpty()) first else "$first/${more.joinToString("/")}"
    return File(path).toPath()
  }

  override fun getPathMatcher(syntaxAndPattern: String): java.nio.file.PathMatcher {
    throw UnsupportedOperationException()
  }

  override fun getUserPrincipalLookupService(): java.nio.file.attribute.UserPrincipalLookupService {
    throw UnsupportedOperationException()
  }

  override fun newWatchService(): java.nio.file.WatchService {
    throw UnsupportedOperationException()
  }

  override fun close() {
    // no-op
  }
}

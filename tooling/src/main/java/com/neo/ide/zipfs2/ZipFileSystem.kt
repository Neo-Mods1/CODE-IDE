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

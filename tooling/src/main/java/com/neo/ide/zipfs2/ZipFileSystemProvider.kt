package com.neo.ide.zipfs2

import java.io.File
import java.net.URI
import java.nio.channels.SeekableByteChannel
import java.nio.file.AccessMode
import java.nio.file.CopyOption
import java.nio.file.DirectoryStream
import java.nio.file.FileStore
import java.nio.file.LinkOption
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.nio.file.spi.FileSystemProvider
import java.util.Collections
import java.util.concurrent.atomic.AtomicBoolean

open class ZipFileSystemProvider : FileSystemProvider() {

  override fun getScheme(): String = "jar"

  override fun newFileSystem(uri: URI, env: MutableMap<String, *>): java.nio.file.FileSystem {
    return ZipFileSystem(this, uri, env)
  }

  override fun getFileSystem(uri: URI): java.nio.file.FileSystem? {
    return null
  }

  override fun getPath(uri: URI): Path {
    return File(uri.path).toPath()
  }

  override fun newByteChannel(
    path: Path,
    options: MutableSet<out OpenOption>,
    vararg attrs: FileAttribute<*>
  ): SeekableByteChannel {
    throw UnsupportedOperationException()
  }

  override fun newDirectoryStream(
    dir: Path,
    filter: DirectoryStream.Filter<in Path>
  ): DirectoryStream<Path> {
    throw UnsupportedOperationException()
  }

  override fun createDirectory(dir: Path, vararg attrs: FileAttribute<*>) {
    throw UnsupportedOperationException()
  }

  override fun delete(path: Path) {
    throw UnsupportedOperationException()
  }

  override fun copy(source: Path, target: Path, vararg options: CopyOption) {
    throw UnsupportedOperationException()
  }

  override fun move(source: Path, target: Path, vararg options: CopyOption) {
    throw UnsupportedOperationException()
  }

  override fun isSameFile(path: Path, path2: Path): Boolean = path == path2

  override fun isHidden(path: Path): Boolean = false

  override fun getFileStore(path: Path): FileStore {
    throw UnsupportedOperationException()
  }

  override fun checkAccess(path: Path, vararg modes: AccessMode) {
    // no-op
  }

  override fun <V : FileAttributeView> getFileAttributeView(
    path: Path,
    type: Class<V>,
    vararg options: LinkOption
  ): V? = null

  override fun <A : BasicFileAttributes> readAttributes(
    path: Path,
    type: Class<A>,
    vararg options: LinkOption
  ): A {
    throw UnsupportedOperationException()
  }

  override fun readAttributes(
    path: Path,
    attributes: String,
    vararg options: LinkOption
  ): MutableMap<String, Any> {
    return mutableMapOf()
  }

  override fun setAttribute(path: Path, attribute: String, value: Any, vararg options: LinkOption) {
    // no-op
  }
}

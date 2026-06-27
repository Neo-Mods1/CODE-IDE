package com.neo.ide.zipfs2

import java.net.URI

class JarFileSystemProvider : ZipFileSystemProvider() {

  override fun getScheme(): String = "jar"

  override fun newFileSystem(uri: URI, env: MutableMap<String, *>): java.nio.file.FileSystem {
    return ZipFileSystem(this, uri, env)
  }
}

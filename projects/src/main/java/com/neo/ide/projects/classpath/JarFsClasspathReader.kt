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

package com.neo.ide.projects.classpath

import com.google.common.collect.ImmutableSet
import com.neo.ide.javac.services.fs.CachedJarFileSystem
import com.neo.ide.javac.services.fs.CachingJarFileSystemProvider
import java.io.File
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitResult.CONTINUE
import java.nio.file.FileVisitResult.SKIP_SUBTREE
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.pathString

/** @author Akash Yadav */
class JarFsClasspathReader : IClasspathReader {

  override fun listClasses(files: Collection<File>): ImmutableSet<ClassInfo> {
    val builder = ImmutableSet.builder<ClassInfo>()
    for (path in files.map(File::toPath)) {
      if (!Files.exists(path)) {
        continue
      }

      val fs = CachingJarFileSystemProvider.newFileSystem(path) as CachedJarFileSystem
      for (rootDirectory in fs.rootDirectories) {
        Files.walkFileTree(
          rootDirectory,
          emptySet(),
          Int.MAX_VALUE,
          object : SimpleFileVisitor<Path>() {

            override fun preVisitDirectory(
              dir: Path?,
              attrs: BasicFileAttributes?
            ): FileVisitResult {
              return if (fs.storeJARPackageDir(dir)) {
                CONTINUE
              } else {
                SKIP_SUBTREE
              }
            }

            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
              var name = file.pathString
              if (name.endsWith("/package-info.class") || !name.endsWith(".class")) {
                return CONTINUE
              }

              name = name.substringBeforeLast(".class")

              if (name.isBlank()) {
                return CONTINUE
              }

              if (name.startsWith('/')) {
                name = name.substring(1)
              }

              if (name.contains('/')) {
                name = name.replace('/', '.')
              }

              ClassInfo.create(name)?.also {
                builder.add(it)
              }

              return super.visitFile(file, attrs)
            }
          }
        )
      }
    }
    return builder.build()
  }
}

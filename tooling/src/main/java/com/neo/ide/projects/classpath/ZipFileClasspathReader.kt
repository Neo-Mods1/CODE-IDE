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
import java.io.File
import java.util.zip.ZipFile

/**
 * Lists all classes from classpath(s).
 *
 * @author Akash Yadav
 */
class ZipFileClasspathReader : IClasspathReader {
  
  override fun listClasses(files: Collection<File>): ImmutableSet<ClassInfo> {
    val classes = ImmutableSet.builder<ClassInfo>()
    files.forEach {
      if (!it.exists()) {
        return@forEach
      }
    
      ZipFile(it).use { zipFile ->
        for (entry in zipFile.entries()) {
          if (!entry.name.endsWith(".class")) {
            continue
          }
        
          var name = entry.name.substringBeforeLast(".class")
          if (name.length <= 1) {
            continue
          }
        
          if (name.startsWith('/')) {
            name = name.substring(1)
          }
        
          if (name.contains('/')) {
            name = name.replace('/', '.')
          }

          ClassInfo.create(name)?.also { classInfo ->
            classes.add(classInfo)
          }
        }
      }
    }
  
    return classes.build()
  }
}

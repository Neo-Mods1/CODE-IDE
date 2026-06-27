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

package com.neo.ide.builder.model

import com.android.builder.model.v2.ide.SourceProvider
import java.io.File
import java.io.Serializable

/** @author Akash Yadav */
class DefaultSourceProvider() : SourceProvider, Serializable {
  private val serialVersionUID = 1L
  override var aidlDirectories: Collection<File>? = null
  override var assetsDirectories: Collection<File>? = null
  override var customDirectories: Collection<DefaultCustomSourceDirectory>? = null
  override var javaDirectories: Collection<File> = emptyList()
  override var jniLibsDirectories: Collection<File> = emptyList()
  override var kotlinDirectories: Collection<File> = emptyList()
  override var manifestFile: File = NoFile
  override var mlModelsDirectories: Collection<File>? = null
  override var name: String = ""
  override var renderscriptDirectories: Collection<File>? = null
  override var resDirectories: Collection<File>? = null
  override var resourcesDirectories: Collection<File> = emptyList()
  override var shadersDirectories: Collection<File>? = null
  override var baselineProfileDirectories: Collection<File>? = null

  companion object {
    @JvmStatic val NoFile = File("<does-not-exist>")
  }
}

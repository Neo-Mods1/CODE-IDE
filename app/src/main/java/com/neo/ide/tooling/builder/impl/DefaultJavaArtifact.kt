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

import com.android.builder.model.v2.ide.BytecodeTransformation
import com.android.builder.model.v2.ide.JavaArtifact
import java.io.File
import java.io.Serializable

/** @author Akash Yadav */
class DefaultJavaArtifact : JavaArtifact, Serializable {

  private val serialVersionUID = 1L
  override var modelSyncFiles: Collection<Void> = emptyList()

  override var assembleTaskName: String = ""
  override var classesFolders: Set<File> = emptySet()
  override var compileTaskName: String = ""
  override var generatedSourceFolders: Collection<File> = emptyList()
  override var ideSetupTaskNames: Set<String> = emptySet()
  override var mockablePlatformJar: File? = null
  override var runtimeResourceFolder: File? = null
  override val generatedClassPaths: Map<String, File> = emptyMap()
  override val bytecodeTransformations: Collection<BytecodeTransformation> = emptyList()
}

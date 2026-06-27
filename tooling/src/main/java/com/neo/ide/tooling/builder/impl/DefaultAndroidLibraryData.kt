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

import com.android.builder.model.v2.ide.AndroidLibraryData
import java.io.File
import java.io.Serializable

/** @author Akash Yadav */
data class DefaultAndroidLibraryData(
  override val aidlFolder: File,
  override val assetsFolder: File,
  override val compileJarFiles: List<File>,
  override val externalAnnotations: File,
  override val jniFolder: File,
  override val manifest: File,
  override val proguardRules: File,
  override val publicResources: File,
  override val renderscriptFolder: File,
  override val resFolder: File,
  override val resStaticLibrary: File,
  override val runtimeJarFiles: List<File>,
  override val symbolFile: File
) : AndroidLibraryData, Serializable {
  private val serialVersionUID = 1L
}

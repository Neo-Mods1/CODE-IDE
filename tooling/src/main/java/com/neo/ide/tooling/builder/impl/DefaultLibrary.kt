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

import com.android.builder.model.v2.ide.Library
import com.android.builder.model.v2.ide.LibraryType
import com.android.builder.model.v2.ide.LibraryType.ANDROID_LIBRARY
import java.io.File
import java.io.Serializable

/** @author Akash Yadav */
class DefaultLibrary : Library, Serializable {
  private val serialVersionUID = 1L
  override var androidLibraryData: DefaultAndroidLibraryData? = null
  override var artifact: File? = null
  override var srcJar: File? = null
  override var docJar: File? = null
  override var samplesJar: File? = null
  override var key: String = ""
  override var libraryInfo: DefaultLibraryInfo? = null
  override var lintJar: File? = null
  override var projectInfo: DefaultProjectInfo? = null
  override var type: LibraryType = ANDROID_LIBRARY

  /** Dependencies of this library. */
  val dependencies = mutableSetOf<String>()

  /**
   * Whether an attempt should be made to lookup this library's package name.
   *
   * FOR INTERNAL USE ONLY!
   */
  var lookupPackage: Boolean = true

  /**
   * The package name of this library. MUST NOT be accesed directly. Use
   * `DefaultLibrary.findPackageName()` method defined in the `:subprojects:tooling-api-models`
   * module.
   */
  var packageName: String = ""
}

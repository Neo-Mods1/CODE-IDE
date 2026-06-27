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

import com.android.builder.model.v2.ide.AndroidArtifact
import com.android.builder.model.v2.ide.JavaArtifact
import com.android.builder.model.v2.ide.Variant
import java.io.File
import java.io.Serializable

/** @author Akash Yadav */
class DefaultVariant : Variant, Serializable {

  private val serialVersionUID = 1L
  @Deprecated("Contained in deviceTestArtifacts")
  override var androidTestArtifact: DefaultAndroidArtifact? = null
  override var displayName: String = ""
  override var isInstantAppCompatible: Boolean = false
  override var desugaredMethods: List<File> = emptyList()
  override var mainArtifact: DefaultAndroidArtifact = DefaultAndroidArtifact()
  override var name: String = ""
  override var testFixturesArtifact: DefaultAndroidArtifact? = null
  override var testedTargetVariant: DefaultTestedTargetVariant? = null
  @Deprecated("Contained in hostTestArtifacts")
  override var unitTestArtifact: DefaultJavaArtifact? = null
  override val runTestInSeparateProcess: Boolean = false
  override val deviceTestArtifacts: Map<String, AndroidArtifact> = emptyMap()
  override val hostTestArtifacts: Map<String, JavaArtifact> = emptyMap()
}

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

import com.android.builder.model.v2.ide.ArtifactDependencies
import com.android.builder.model.v2.models.VariantDependencies
import java.io.Serializable

/** @author Akash Yadav */
class DefaultVariantDependencies : VariantDependencies, Serializable {

  private val serialVersionUID = 1L
  @Deprecated("Contained in deviceTestArtifacts")
  override var androidTestArtifact: DefaultArtifactDependencies? = null
  override var libraries: Map<String, DefaultLibrary> = emptyMap()
  override var mainArtifact: DefaultArtifactDependencies = DefaultArtifactDependencies()
  override var name: String = ""
  override var testFixturesArtifact: DefaultArtifactDependencies? = null
  @Deprecated("Contained in hostTestArtifacts")
  override var unitTestArtifact: DefaultArtifactDependencies? = null
  override val deviceTestArtifacts: Map<String, ArtifactDependencies> = emptyMap()
  override val hostTestArtifacts: Map<String, ArtifactDependencies> = emptyMap()
}

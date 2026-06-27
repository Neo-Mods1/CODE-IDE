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
import com.android.builder.model.v2.ide.SourceSetContainer
import java.io.Serializable

/** @author Akash Yadav */
class DefaultSourceSetContainer : SourceSetContainer, Serializable {

  private val serialVersionUID = 1L
  @Deprecated("Contained in deviceTestSourceProviders")
  override var androidTestSourceProvider: DefaultSourceProvider? = null
  override var sourceProvider: DefaultSourceProvider = DefaultSourceProvider()
  override var testFixturesSourceProvider: DefaultSourceProvider? = null
  @Deprecated("Contained in hostTestSourceProviders")
  override var unitTestSourceProvider: DefaultSourceProvider? = null
  override val deviceTestSourceProviders: Map<String, SourceProvider> = emptyMap()
  override val hostTestSourceProviders: Map<String, SourceProvider> = emptyMap()
}

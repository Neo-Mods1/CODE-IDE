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



package com.neo.ide.editor.schemes

import io.github.rosemoe.sora.editor.ts.LocalsCaptureSpec
import org.slf4j.LoggerFactory

/**
 * Provides local
 *
 * @author Akash Yadav
 */
object LocalCaptureSpecProvider {

  private val log = LoggerFactory.getLogger(LocalCaptureSpecProvider::class.java)

  @JvmStatic
  fun newLocalCaptureSpec(type: String): LocalsCaptureSpec {
    val lang =
      IDEColorSchemeProvider.getColorSchemeForType(type)?.languages?.get(type)
        ?: run {
          log.error(
            "Cannot create LocalsCaptureSpec. Failed to load current color scheme. Falling back to default implementation"
          )
          return LocalsCaptureSpec.DEFAULT
        }
    return object : LocalsCaptureSpec() {

      override fun isDefinitionCapture(captureName: String): Boolean {
        return lang.isLocalDef(captureName)
      }

      override fun isDefinitionValueCapture(captureName: String): Boolean {
        return lang.isLocalDefVal(captureName)
      }

      override fun isReferenceCapture(captureName: String): Boolean {
        return lang.isLocalRef(captureName)
      }

      override fun isScopeCapture(captureName: String): Boolean {
        return lang.isLocalScope(captureName)
      }

      override fun isMembersScopeCapture(captureName: String): Boolean {
        return lang.isMembersScope(captureName)
      }
    }
  }
}

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



package com.neo.ide.editor.language.treesitter

import android.content.Context
import com.neo.ide.editor.language.treesitter.TreeSitterLanguage.Factory
import com.neo.ide.treesitter.kotlin.TSLanguageKotlin
import io.github.rosemoe.sora.lang.Language.INTERRUPTION_LEVEL_STRONG

/**
 * [TreeSitterLanguage] implementation for Kotlin.
 *
 * @author Akash Yadav
 */
open class KotlinLanguage(context: Context) :
  TreeSitterLanguage(context, TSLanguageKotlin.getInstance(), TS_TYPE_KT) {

  companion object {

    val FACTORY = Factory { KotlinLanguage(it) }
    const val TS_TYPE_KT = "kt"
    const val TS_TYPE_KTS = "kts"
  }

  override fun getInterruptionLevel(): Int {
    return INTERRUPTION_LEVEL_STRONG
  }
}

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



package com.neo.ide.javac.services.compiler

import com.neo.ide.javac.services.NBJavaCompiler
import openjdk.tools.javac.main.JavaCompiler
import openjdk.tools.javac.util.Context

/**
 * Reusable JavaCompiler; exposes a method to clean up the component from leftovers associated
 * with previous compilations.
 */
open class ReusableJavaCompiler(context: Context?) : NBJavaCompiler(context) {
  
  companion object {
    val factory = Context.Factory<JavaCompiler> { ReusableJavaCompiler(it) }
  }
  
  override fun checkReusable() {
    // Do nothing
  }
  
  override fun close() {
    // Do nothing
  }
  
  fun clear()   {
    newRound()
  }
}
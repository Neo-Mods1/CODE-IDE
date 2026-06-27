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

import com.neo.ide.javac.services.NBLog
import jdkx.tools.Diagnostic
import jdkx.tools.DiagnosticListener
import jdkx.tools.JavaFileObject
import openjdk.tools.javac.util.Context
import openjdk.tools.javac.util.DefinedBy
import openjdk.tools.javac.util.DefinedBy.Api.COMPILER
import openjdk.tools.javac.util.Log
import java.io.PrintWriter

/**
 * Reusable Log; exposes a method to clean up the component from leftovers associated with
 * previous compilations.
 */
internal class ReusableLog(var context: Context) : NBLog(context, PrintWriter(System.err)) {
  fun clear() {
    recorded.clear()
    sourceMap.clear()
    nerrors = 0
    nwarnings = 0
    
    // Set a fake listener that will lazily lookup the context for the 'real' listener.
    // Since
    // this field is never updated when a new task is created, we cannot simply reset
    // the field
    // or keep old value. This is a hack to workaround the limitations in the current
    // infrastructure.
    diagListener = object : DiagnosticListener<JavaFileObject?> {
      
      var cachedListener: DiagnosticListener<JavaFileObject>? = null
      
      @Suppress("UNCHECKED_CAST")
      @DefinedBy(COMPILER)
      override fun report(diagnostic: Diagnostic<out JavaFileObject>) {
        if (cachedListener == null) {
          cachedListener = context.get(DiagnosticListener::class.java) as DiagnosticListener<JavaFileObject>?
        }
        cachedListener!!.report(diagnostic)
      }
    }
  }
  
  companion object {
    val factory = Context.Factory<Log> { context: Context -> ReusableLog(context) }
  }
}
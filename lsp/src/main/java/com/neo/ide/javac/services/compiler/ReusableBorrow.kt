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

import openjdk.tools.javac.api.JavacTaskImpl

/** @author Akash Yadav */
class ReusableBorrow
internal constructor(
  private val reusableCompiler: ReusableCompiler,
  @JvmField val task: JavacTaskImpl
) : AutoCloseable {

  private var closed = false

  override fun close() {
    if (closed) {
      return
    }
    // not returning the context to the pool if task crashes with an exception
    // the task/context may be in a broken state
    reusableCompiler.currentContext!!.clear()
    task.cleanup()
    reusableCompiler.checkedOut = false
    closed = true
  }
}

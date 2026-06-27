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



package com.neo.ide.lsp.java.compiler

import com.neo.ide.models.Position
import jdkx.tools.JavaFileObject
import openjdk.tools.javac.util.Context

/**
 * Configuration for the [JavaCompilerImpl].
 *
 * @property files The main files that are being compiled/parsed.
 * @property completionInfo Information about the completion
 * @author Akash Yadav
 */
class JavaCompilerConfig(context: Context) {
  init {
    context.put(compilerConfigKey, this)
  }

  var files: Collection<JavaFileObject>? = null
  var completionInfo: CompletionInfo? = null

  companion object {

    @JvmField val compilerConfigKey = Context.Key<JavaCompilerConfig>()

    @JvmStatic
    fun instance(context: Context): JavaCompilerConfig {
      var instance = context.get(compilerConfigKey)
      if (instance == null) {
        instance = JavaCompilerConfig(context)
      }
      return instance
    }
  }
}

/**
 * Information about the completion request initiated by the Java completion provider.
 *
 * @property cursor The cursor position for the completion.
 * @author Akash Yadav
 */
data class CompletionInfo(val cursor: Position)

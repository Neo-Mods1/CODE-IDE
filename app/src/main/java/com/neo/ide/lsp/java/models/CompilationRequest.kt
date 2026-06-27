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



package com.neo.ide.lsp.java.models

import com.neo.ide.lsp.java.compiler.CompilationTaskProcessor
import com.neo.ide.lsp.java.compiler.DefaultCompilationTaskProcessor
import jdkx.tools.JavaFileObject
import openjdk.tools.javac.util.Context
import java.util.function.Consumer

/**
 * Data sent to compiler to request a compilation.
 *
 * @param sources The source files to compile.
 * @param partialRequest Data that will be used to a partial reparse.
 * @author Akash Yadav
 */
data class CompilationRequest
@JvmOverloads
constructor(
  @JvmField val sources: Collection<JavaFileObject>,
  @JvmField val partialRequest: PartialReparseRequest? = null,
  @JvmField
  val compilationTaskProcessor: CompilationTaskProcessor = DefaultCompilationTaskProcessor(),
  @JvmField var configureContext: Consumer<Context>? = null
)

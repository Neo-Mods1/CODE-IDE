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



package com.neo.ide.lsp.java.providers

import com.neo.ide.lookup.Lookup
import com.neo.ide.lsp.api.IServerSettings
import com.neo.ide.lsp.java.compiler.JavaCompilerService
import com.neo.ide.progress.ICancelChecker
import java.nio.file.Path

/**
 * Base class for java service providers.
 *
 * @author Akash Yadav
 */
abstract class BaseJavaServiceProvider(
  protected val file: Path,
  protected val compiler: JavaCompilerService,
  protected val settings: IServerSettings
) {

  /** Abort the completion if cancelled. */
  fun abortCompletionIfCancelled() {
    val checker = Lookup.getDefault().lookup(ICancelChecker::class.java)
    checker?.abortIfCancelled()
  }
}

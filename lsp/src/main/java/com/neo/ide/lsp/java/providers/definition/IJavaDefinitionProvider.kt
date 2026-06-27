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



package com.neo.ide.lsp.java.providers.definition

import com.neo.ide.lsp.api.IServerSettings
import com.neo.ide.lsp.java.compiler.JavaCompilerService
import com.neo.ide.lsp.java.providers.BaseJavaServiceProvider
import com.neo.ide.lsp.java.providers.DefinitionProvider
import com.neo.ide.models.Location
import com.neo.ide.models.Position
import com.neo.ide.progress.ICancelChecker
import jdkx.lang.model.element.Element
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

/**
 * Provides definition for a specific symbol in Java source code.
 *
 * @author Akash Yadav
 */
abstract class IJavaDefinitionProvider(
  protected val position: Position,
  completingFile: Path,
  compiler: JavaCompilerService,
  settings: IServerSettings,
  cancelChecker: ICancelChecker
) : BaseJavaServiceProvider(completingFile, compiler, settings), ICancelChecker by cancelChecker {

  protected val line = position.line
  protected val column = position.column

  companion object {

    @JvmStatic
    protected val log: Logger = LoggerFactory.getLogger(IJavaDefinitionProvider::class.java)
  }

  /**
   * Finds the definition for the given element.
   * @param element The element to find definition for.
   */
  fun findDefinition(element: Element?): List<Location> {
    if (element == null) {
      return DefinitionProvider.NOT_SUPPORTED
    }

    return doFindDefinition(element)
  }

  abstract fun doFindDefinition(element: Element): List<Location>
}

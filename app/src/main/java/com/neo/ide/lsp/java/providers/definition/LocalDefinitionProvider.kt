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
import com.neo.ide.lsp.java.utils.FindHelper
import com.neo.ide.models.Location
import com.neo.ide.models.Position
import com.neo.ide.progress.ICancelChecker
import jdkx.lang.model.element.Element
import openjdk.source.util.Trees
import java.nio.file.Path

/**
 * Provides definition for local elements.
 *
 * @author Akash Yadav
 */
class LocalDefinitionProvider(
  position: Position,
  completingFile: Path,
  compiler: JavaCompilerService,
  settings: IServerSettings, cancelChecker: ICancelChecker,
) : IJavaDefinitionProvider(position, completingFile, compiler, settings, cancelChecker) {

  override fun doFindDefinition(element: Element): List<Location> {
    return compiler.compile(file).get {
      val trees = Trees.instance(it.task)
      val path = trees.getPath(element)
      if (path == null) {
        log.error("TreePath of element is null. Cannot find definition. Element is {}", element)
        return@get emptyList<Location>()
      }

      var name = element.simpleName
      if (name.contentEquals("<init>")) {
        name = element.enclosingElement.simpleName
      }

      abortIfCancelled()
      return@get listOf(FindHelper.location(it, path, name))
    }
  }
}

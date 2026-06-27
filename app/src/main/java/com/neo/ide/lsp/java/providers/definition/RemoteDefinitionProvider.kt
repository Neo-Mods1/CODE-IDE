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
import com.neo.ide.models.Location
import com.neo.ide.models.Position
import com.neo.ide.progress.ICancelChecker
import jdkx.lang.model.element.Element
import jdkx.tools.JavaFileObject
import java.nio.file.Path

/**
 * Finds definition of an element in other source locations.
 *
 * @author Akash Yadav
 */
class RemoteDefinitionProvider(
  position: Position,
  completingFile: Path,
  compiler: JavaCompilerService,
  settings: IServerSettings, cancelChecker: ICancelChecker,
) : IJavaDefinitionProvider(position, completingFile, compiler, settings, cancelChecker) {

  private lateinit var otherFile: JavaFileObject

  fun setOtherFile(jfo: JavaFileObject): RemoteDefinitionProvider {
    this.otherFile = jfo
    return this
  }

  override fun doFindDefinition(element: Element): List<Location> {
//    val task = compiler.compile(listOf(SourceFileObject(file), otherFile))
    val provider = LocalDefinitionProvider(position, file, compiler, settings, this)
    return provider.findDefinition(element)
//    return provider
//      .findDefinition(task.get { NavigationHelper.findElement(it, file, line, column) })
  }
}

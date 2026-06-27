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
import com.neo.ide.lsp.java.compiler.SourceFileObject
import com.neo.ide.lsp.java.providers.DefinitionProvider
import com.neo.ide.lsp.java.utils.FindHelper
import com.neo.ide.models.Location
import com.neo.ide.models.Position
import com.neo.ide.progress.ICancelChecker
import com.neo.ide.utils.DocumentUtils.isSameFile
import jdkx.lang.model.element.Element
import jdkx.lang.model.element.TypeElement
import jdkx.tools.JavaFileObject
import openjdk.source.util.Trees
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Finds definition for erroneous elements.
 *
 * @author Akash Yadav
 */
class ErroneousDefinitionProvider(
  position: Position,
  completingFile: Path,
  compiler: JavaCompilerService,
  settings: IServerSettings, cancelChecker: ICancelChecker,
) : IJavaDefinitionProvider(position, completingFile, compiler, settings, cancelChecker) {

  override fun doFindDefinition(element: Element): List<Location> {
    val name = element.simpleName ?: return DefinitionProvider.NOT_SUPPORTED
    val parent = element.enclosingElement as? TypeElement ?: return DefinitionProvider.NOT_SUPPORTED
    val className = parent.qualifiedName.toString()
    val memberName = name.toString()
    return findAllMembers(className, memberName)
  }

  private fun findAllMembers(className: String, memberName: String): List<Location> {
    val otherFile = compiler.findAnywhere(className)
    abortIfCancelled()
    if (!otherFile.isPresent) {
      log.error("Cannot find source file for class: {}", className)
      return emptyList()
    }

    val fileAsSource = SourceFileObject(file)
    var sources = listOf(fileAsSource, otherFile.get())
    if (isSameFile(Paths.get(otherFile.get().toUri()), file)) {
      sources = listOf<JavaFileObject>(fileAsSource)
    }

    abortIfCancelled()

    return compiler.compile(sources).get { task ->
      val locations = mutableListOf<Location>()
      val trees = Trees.instance(task.task)
      val elements = task.task.elements
      val parentClass = elements.getTypeElement(className)

      abortIfCancelled()
      for (member in elements.getAllMembers(parentClass)) {
        if (!member.simpleName.contentEquals(memberName)) continue
        val path = trees.getPath(member) ?: continue
        val location = FindHelper.location(task, path, memberName)
        abortIfCancelled()
        locations.add(location)
      }

      locations
    }
  }
}

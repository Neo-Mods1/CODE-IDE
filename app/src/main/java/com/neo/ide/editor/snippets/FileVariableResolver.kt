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



package com.neo.ide.editor.snippets

import com.neo.ide.editor.ui.IDEEditor
import com.neo.ide.projects.IProjectManager
import io.github.rosemoe.sora.widget.snippet.variable.FileBasedSnippetVariableResolver

/**
 * Resolver for resolving snippet variables related to the file opened in an editor.
 *
 * @author Akash Yadav
 */
class FileVariableResolver(editor: IDEEditor) : FileBasedSnippetVariableResolver(), AbstractSnippetVariableResolver {

  var editor: IDEEditor? = editor
    private set

  companion object {
    private const val TM_FILENAME = "TM_FILENAME"
    private const val TM_FILENAME_BASE = "TM_FILENAME_BASE"
    private const val TM_DIRECTORY = "TM_DIRECTORY"
    private const val TM_FILEPATH = "TM_FILEPATH"
    private const val RELATIVE_FILEPATH = "RELATIVE_FILEPATH"
  }

  override fun resolve(name: String): String {
    val file = editor?.file ?: return ""
    return when (name) {
      TM_FILENAME -> file.name
      TM_FILENAME_BASE -> file.nameWithoutExtension
      TM_DIRECTORY -> file.parentFile?.absolutePath ?: ""
      TM_FILEPATH -> file.absolutePath
      RELATIVE_FILEPATH -> file.relativeTo(IProjectManager.getInstance().projectDir).absolutePath
      else -> ""
    }
  }

  override fun getResolvableNames(): Array<String> {
    return arrayOf(TM_FILENAME, TM_FILENAME_BASE, TM_DIRECTORY, TM_FILEPATH, RELATIVE_FILEPATH)
  }

  override fun close() {
    editor = null
  }
}

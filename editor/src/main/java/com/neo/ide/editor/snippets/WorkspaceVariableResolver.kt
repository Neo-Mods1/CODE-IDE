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

import com.neo.ide.projects.IProjectManager
import io.github.rosemoe.sora.widget.snippet.variable.WorkspaceBasedSnippetVariableResolver

/**
 * Resolver for resolving snippet variables related to the opened workspace folder (project).
 *
 * @author Akash Yadav
 */
class WorkspaceVariableResolver :
  WorkspaceBasedSnippetVariableResolver(), AbstractSnippetVariableResolver {

  companion object {

    private const val WORKSPACE_NAME = "WORKSPACE_NAME"
    private const val WORKSPACE_FOLDER = "WORKSPACE_FOLDER"
  }

  override fun resolve(name: String): String {
    val directory = IProjectManager.getInstance().projectDir
    return when (name) {
      WORKSPACE_NAME -> directory.name
      WORKSPACE_FOLDER -> directory.absolutePath
      else -> ""
    }
  }

  override fun getResolvableNames(): Array<String> {
    return arrayOf(WORKSPACE_NAME, WORKSPACE_FOLDER)
  }
}

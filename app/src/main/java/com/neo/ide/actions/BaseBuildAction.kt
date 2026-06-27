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



package com.neo.ide.actions

import com.neo.ide.lookup.Lookup
import com.neo.ide.projects.builder.BuildService

/**
 * Marker class for actions that execute build related tasks.
 *
 * @author Akash Yadav
 */
abstract class BaseBuildAction : EditorActivityAction() {

  protected val buildService: BuildService?
    get() = Lookup.getDefault().lookup(BuildService.KEY_BUILD_SERVICE)

  override fun prepare(data: ActionData) {
    super.prepare(data)
    val context = data.getActivity()
    if (context == null) {
      visible = false
      return
    } else {
      visible = true
    }

    enabled = buildService?.let { !it.isBuildInProgress } == true
  }
}

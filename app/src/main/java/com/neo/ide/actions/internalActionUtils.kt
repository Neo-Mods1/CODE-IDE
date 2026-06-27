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

/*
 *  This file is part of AndroidIDE.
 *  
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.neo.ide.actions

import android.content.Context
import com.neo.ide.R
import com.neo.ide.projects.IProjectManager
import com.neo.ide.projects.android.AndroidModule
import com.neo.ide.utils.DialogUtils
import com.neo.ide.utils.ILogger
import com.neo.ide.utils.flashError

/**
 * @see openApplicationModuleChooser
 */
inline fun openApplicationModuleChooser(data: ActionData,
  crossinline callback: (AndroidModule) -> Unit) =
  openApplicationModuleChooser(data.requireContext(), callback)

/**
 * Shows a dialog to let the user choose between Android application modules in case the project has
 * multiple subproject with `com.android.application` plugin. If the project contains only a single
 * application module, it is selected by default and the dialog is not shown to the user.
 *
 * @param
 */
inline fun openApplicationModuleChooser(context: Context,
  crossinline callback: (AndroidModule) -> Unit) {
  val applications = IProjectManager.getInstance()
    .getWorkspace()
    ?.androidProjects()
    ?.filter(AndroidModule::isApplication)
    ?.toList() ?: emptyList()

  if (applications.isEmpty()) {
    flashError(R.string.msg_launch_failure_no_app_module)
    ILogger.ROOT.error("Cannot run application. No application modules found in project.")
    return
  }

  if (applications.size == 1) {
    // Only one application module in available in the project.
    callback(applications.first())
    return
  }

  // there are multiple application modules in the project
  // ask the user to select the application module to build
  val builder = DialogUtils.newSingleChoiceDialog(
    context,
    context.getString(R.string.title_choose_application),
    applications.map { it.path }.toTypedArray(),
    0
  ) { selection ->
    val app = applications[selection]
    ILogger.ROOT.info("Selected application: '{}'", app.path)
    callback(app)
  }

  builder.show()
}
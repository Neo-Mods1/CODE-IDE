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

package com.neo.ide.projects

import com.neo.ide.tooling.api.ProjectType
import com.neo.ide.tooling.api.models.GradleTask
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * A Gradle project model which is identical to [IGradleProject][com.neo.ide.tooling.api.IGradleProject]. This project module caches all the data
 * from an [IGradleProject][com.neo.ide.tooling.api.IGradleProject] eliminating the use of [CompletableFuture] s.
 *
 * @param name The display name of the project.
 * @param description The project description.
 * @param path The project path (same as Gradle project paths). For example, `:app`,
 *   `:module:submodule`, etc. Root project is always represented by path `:`.
 * @param projectDir The project directory.
 * @param buildDir The build directory of the project.
 * @param buildScript The Gradle buildscript file of the project.
 * @param tasks The tasks of the project.
 * @author Akash Yadav
 */
open class GradleProject(
  val name: String,
  val description: String,
  val path: String,
  val projectDir: File,
  val buildDir: File,
  val buildScript: File,
  val tasks: List<GradleTask>
) {

  var type: ProjectType = ProjectType.Gradle
    protected set
}

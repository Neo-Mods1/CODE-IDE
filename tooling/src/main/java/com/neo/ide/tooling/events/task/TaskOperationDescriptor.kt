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

package com.neo.ide.tooling.events.task

import com.neo.ide.tooling.events.OperationDescriptor
import com.neo.ide.tooling.model.PluginIdentifier

/** @author Akash Yadav */
class TaskOperationDescriptor(
  val dependencies: Set<OperationDescriptor>,
  val originPlugin: PluginIdentifier,
  val taskPath: String,
  override val name: String,
  override val displayName: String
) : OperationDescriptor() {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is TaskOperationDescriptor) return false

    if (dependencies != other.dependencies) return false
    if (originPlugin != other.originPlugin) return false
    if (taskPath != other.taskPath) return false
    if (name != other.name) return false
    if (displayName != other.displayName) return false

    return true
  }

  override fun hashCode(): Int {
    var result = dependencies.hashCode()
    result = 31 * result + originPlugin.hashCode()
    result = 31 * result + taskPath.hashCode()
    result = 31 * result + name.hashCode()
    result = 31 * result + displayName.hashCode()
    return result
  }
}

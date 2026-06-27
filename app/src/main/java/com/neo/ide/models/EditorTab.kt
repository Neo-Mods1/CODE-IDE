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

package com.neo.ide.models

import java.io.File

data class EditorTab(
    val id: String = System.currentTimeMillis().toString(),
    val file: File,
    val fileName: String = file.name,
    val filePath: String = file.absolutePath,
    val extension: String = file.extension.lowercase(),
    var isDirty: Boolean = false,
    var isActive: Boolean = false,
    var scrollPosition: Int = 0,
    var cursorPosition: Int = 0
) {
    val fileType: FileType
        get() = FileType.fromExtension(extension)

    fun markDirty() {
        isDirty = true
    }

    fun markClean() {
        isDirty = false
    }

    fun updateScrollPosition(position: Int) {
        scrollPosition = position
    }

    fun updateCursorPosition(position: Int) {
        cursorPosition = position
    }
}

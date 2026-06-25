/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
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

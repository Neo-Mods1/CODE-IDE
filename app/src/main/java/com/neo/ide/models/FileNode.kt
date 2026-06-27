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

data class FileNode(
    val file: File,
    val name: String = file.name,
    val isDirectory: Boolean = file.isDirectory,
    val depth: Int = 0,
    var isExpanded: Boolean = false,
    val children: MutableList<FileNode> = mutableListOf(),
    val extension: String = if (!isDirectory) file.extension.lowercase() else "",
    val isHidden: Boolean = file.isHidden
) {
    val hasChildren: Boolean
        get() = children.isNotEmpty()

    fun addChild(child: FileNode) {
        children.add(child)
    }

    fun removeChild(child: FileNode) {
        children.remove(child)
    }

    fun findChild(name: String): FileNode? {
        return children.find { it.name == name }
    }

    fun getAllDescendants(): List<FileNode> {
        val result = mutableListOf<FileNode>()
        for (child in children) {
            result.add(child)
            if (child.isDirectory) {
                result.addAll(child.getAllDescendants())
            }
        }
        return result
    }

    fun getFileType(): FileType {
        return when {
            isDirectory -> FileType.FOLDER
            else -> FileType.fromExtension(extension)
        }
    }
}

enum class FileType(val extension: String, val displayName: String) {
    KOTLIN("kt", "Kotlin"),
    JAVA("java", "Java"),
    XML("xml", "XML"),
    GRADLE("gradle", "Gradle"),
    KTS("kts", "Kotlin Script"),
    JSON("json", "JSON"),
    MARKDOWN("md", "Markdown"),
    PROPERTIES("properties", "Properties"),
    PNG("png", "PNG Image"),
    JPG("jpg", "JPEG Image"),
    JPEG("jpeg", "JPEG Image"),
    GIF("gif", "GIF Image"),
    WEBP("webp", "WebP Image"),
    SVG("svg", "SVG Image"),
    TXT("txt", "Text"),
    SH("sh", "Shell Script"),
    PYTHON("py", "Python"),
    CPP("cpp", "C++"),
    C("c", "C"),
    H("h", "Header"),
    FOLDER("", "Folder"),
    FILE("", "File");

    companion object {
        fun fromExtension(ext: String): FileType {
            return entries.find { it.extension.equals(ext, ignoreCase = true) } ?: FILE
        }
    }
}

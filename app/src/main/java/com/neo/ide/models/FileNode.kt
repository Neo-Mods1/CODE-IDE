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

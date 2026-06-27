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

package com.neo.ide.utils

import com.neo.ide.models.FileNode
import java.io.File

object FileUtils {

    fun buildFileTree(root: File, maxDepth: Int = 10): List<FileNode> {
        val result = mutableListOf<FileNode>()
        if (!root.exists() || !root.isDirectory) return result

        val rootNodes = buildChildren(root, 0, maxDepth)
        result.addAll(rootNodes)
        return result
    }

    private fun buildChildren(directory: File, depth: Int, maxDepth: Int): List<FileNode> {
        if (depth > maxDepth) return emptyList()

        val children = mutableListOf<FileNode>()
        val files = directory.listFiles() ?: return emptyList()

        val sortedFiles = files.sortedWith(compareByDescending<File> { it.isDirectory }.thenBy { it.name })

        for (file in sortedFiles) {
            if (file.name.startsWith(".")) continue

            val node = FileNode(
                file = file,
                depth = depth
            )

            if (file.isDirectory) {
                val subChildren = buildChildren(file, depth + 1, maxDepth)
                node.children.addAll(subChildren)
                node.isExpanded = depth < 1
            }

            children.add(node)
        }

        return children
    }

    fun flattenTree(nodes: List<FileNode>): List<FileNode> {
        val result = mutableListOf<FileNode>()
        for (node in nodes) {
            result.add(node)
            if (node.isDirectory && node.isExpanded) {
                result.addAll(flattenTree(node.children))
            }
        }
        return result
    }

    fun getFileExtension(filename: String): String {
        val lastDot = filename.lastIndexOf('.')
        return if (lastDot != -1) {
            filename.substring(lastDot + 1).lowercase()
        } else {
            ""
        }
    }

    fun getFileMimeType(filename: String): String {
        val extension = getFileExtension(filename)
        return when (extension) {
            "kt" -> "text/x-kotlin"
            "java" -> "text/x-java"
            "xml" -> "text/xml"
            "gradle", "kts" -> "text/x-gradle"
            "json" -> "application/json"
            "md" -> "text/markdown"
            "txt" -> "text/plain"
            "png", "jpg", "jpeg", "gif", "webp" -> "image/*"
            else -> "text/plain"
        }
    }

    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }

    fun isValidProjectRoot(directory: File): Boolean {
        if (!directory.exists() || !directory.isDirectory) return false

        val hasGradle = directory.listFiles()?.any {
            it.name == "build.gradle" || it.name == "build.gradle.kts"
        } ?: false

        val hasAndroidManifest = directory.listFiles()?.any {
            it.name == "AndroidManifest.xml"
        } ?: false

        val hasSrc = File(directory, "src").exists()

        return hasGradle || hasAndroidManifest || hasSrc
    }

    fun findProjectRoot(startPath: String): File? {
        var current = File(startPath)
        while (current.exists()) {
            if (isValidProjectRoot(current)) {
                return current
            }
            current = current.parentFile ?: break
        }
        return null
    }

    fun readFileContent(file: File): String {
        return try {
            if (file.exists() && file.canRead()) {
                file.readText()
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    fun writeFileContent(file: File, content: String): Boolean {
        return try {
            file.parentFile?.mkdirs()
            file.writeText(content)
            true
        } catch (e: Exception) {
            false
        }
    }
}

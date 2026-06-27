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



package com.neo.ide.lsp.java.visitors

import openjdk.source.tree.CompilationUnitTree
import openjdk.source.tree.MethodTree
import openjdk.source.util.JavacTask
import openjdk.source.util.TreePath
import openjdk.source.util.TreePathScanner
import openjdk.source.util.Trees

/**
 * Finds method declaration at the given cursor position.
 *
 * @author Akash Yadav
 */
class FindMethodAt(val task: JavacTask) : TreePathScanner<TreePath?, Long>() {

  private val sourcePositions = Trees.instance(task).sourcePositions
  private var root: CompilationUnitTree? = null

  override fun visitCompilationUnit(node: CompilationUnitTree?, p: Long): TreePath? {
    this.root = node
    return super.visitCompilationUnit(node, p)
  }

  override fun visitMethod(node: MethodTree?, p: Long): TreePath? {
    val smaller = super.visitMethod(node, p)
    if (smaller != null || node == null) {
      return smaller
    }

    if (node.body != null) {
      val bodyStart = sourcePositions.getStartPosition(root, node.body)
      val bodyEnd = sourcePositions.getEndPosition(root, node.body)
      if (p in bodyStart..bodyEnd) {
        return currentPath
      }
    }

    val start = sourcePositions.getStartPosition(root, node)
    val end = sourcePositions.getEndPosition(root, node)
    if (p in start..end) {
      return currentPath
    }

    return null
  }
}

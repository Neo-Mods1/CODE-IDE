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



package com.neo.ide.javac.services.visitors

import com.neo.ide.javac.services.visitors.FindAnonymousVisitor.Mode.CHECK
import com.neo.ide.javac.services.visitors.FindAnonymousVisitor.Mode.COLLECT
import openjdk.source.tree.ClassTree
import openjdk.source.tree.MethodTree
import openjdk.source.tree.Tree
import openjdk.source.tree.VariableTree

/**
 * Partial reparse helper visitor. Finds anonymous and local classes in given method tree.
 *
 * @author Akash Yadav
 */
class FindAnonymousVisitor : ErrorAwareTreeScanner<Unit, Unit>() {

  private enum class Mode {
    COLLECT,
    CHECK
  }

  var noInner = 0
  var hasLocalClass = false
  val docOwners: MutableSet<Tree> = HashSet<Tree>()
  private var mode = COLLECT

  fun reset() {
    noInner = 0
    hasLocalClass = false
    mode = CHECK
  }

  override fun visitClass(node: ClassTree, p: Unit?): Unit? {
    if (node.simpleName.isNotEmpty()) {
      hasLocalClass = true
    }

    noInner++
    handleDoc(node)
    return super.visitClass(node, p)
  }

  override fun visitMethod(node: MethodTree, p: Unit?): Unit? {
    handleDoc(node)
    return super.visitMethod(node, p)
  }

  override fun visitVariable(node: VariableTree, p: Unit?): Unit? {
    handleDoc(node)
    return super.visitVariable(node, p)
  }

  private fun handleDoc(tree: Tree) {
    if (mode == COLLECT) {
      docOwners.add(tree)
    }
  }
}

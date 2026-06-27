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



package com.neo.ide.lsp.java.utils

import openjdk.source.tree.MethodTree
import openjdk.source.tree.Tree
import openjdk.source.tree.Tree.Kind.ANNOTATION_TYPE
import openjdk.source.tree.Tree.Kind.CLASS
import openjdk.source.tree.Tree.Kind.ENUM
import openjdk.source.tree.Tree.Kind.INTERFACE
import openjdk.source.tree.Tree.Kind.METHOD

/**
 * Utility methods for Javac Trees.
 *
 * @author Akash Yadav
 */
class TreeUtils {

  companion object {
    @JvmStatic
    fun isType(tree: Tree?): Boolean {
      return isType(tree?.kind)
    }

    @JvmStatic
    fun isType(kind: Tree.Kind?): Boolean {
      kind ?: return false

      return when (kind) {
        CLASS,
        INTERFACE,
        ANNOTATION_TYPE,
        ENUM -> true
        else -> false
      }
    }

    @JvmStatic
    fun isMethod(tree: Tree?): Boolean {
      return isMethod(tree?.kind)
    }

    @JvmStatic
    fun isMethod(kind: Tree.Kind?): Boolean {
      return kind == METHOD
    }

    @JvmStatic
    fun isConstructor(tree: Tree?): Boolean {
      tree ?: return false
      return tree.kind == METHOD && (tree as MethodTree).name.contentEquals("<init>")
    }

    @JvmStatic
    fun isMethodOrConstructor(tree: Tree?): Boolean {
      return isMethod(tree) || isConstructor(tree)
    }
  }
}

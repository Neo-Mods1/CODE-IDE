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

import com.neo.ide.lsp.java.compiler.CompileTask
import com.neo.ide.lsp.java.parser.ParseTask
import com.neo.ide.models.Position
import openjdk.source.tree.CompilationUnitTree
import openjdk.source.tree.Tree
import openjdk.source.util.JavacTask
import openjdk.source.util.Trees

/** @author Akash Yadav */
fun positionForImports(className: String, task: ParseTask): Position {
  return positionForImports(className, task.task, task.root)
}

fun positionForImports(className: String, task: CompileTask): Position {
  return positionForImports(className, task.task, task.root())
}

fun positionForImports(className: String, task: JavacTask, root: CompilationUnitTree): Position {
  val imports = root.imports
  for (i in imports) {
    val next = i.qualifiedIdentifier.toString()
    if (className < next) {
      return insertBefore(task, root, i)
    }
  }
  if (imports.isNotEmpty()) {
    val last = imports[imports.size - 1]
    return insertAfter(task, root, last)
  }

  return if (root.getPackage() != null) {
    insertAfter(task, root, root.getPackage())
  } else Position(0, 0)
}

fun insertBefore(task: JavacTask, root: CompilationUnitTree, tree: Tree): Position {
  val pos = Trees.instance(task).sourcePositions
  val offset = pos.getStartPosition(root, tree)
  val line = root.lineMap.getLineNumber(offset).toInt()
  return Position(line - 1, 0)
}

fun insertAfter(task: JavacTask, root: CompilationUnitTree, tree: Tree): Position {
  val pos = Trees.instance(task).sourcePositions
  val offset = pos.getStartPosition(root, tree)
  val line = root.lineMap.getLineNumber(offset).toInt()
  return Position(line, 0)
}

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



package com.neo.ide.lsp.java.compiler

import com.neo.ide.utils.StopWatch
import openjdk.source.tree.CompilationUnitTree
import openjdk.tools.javac.api.JavacTaskImpl
import java.util.function.Consumer

/**
 * Default implementation of [CompilationTaskProcessor].
 *
 * @author Akash Yadav
 */
class DefaultCompilationTaskProcessor : CompilationTaskProcessor {

  override fun process(task: JavacTaskImpl, processCompilationUnit: Consumer<CompilationUnitTree>) {
    val watch = StopWatch("Process compilation task")
    val trees = task.parse()
    watch.lapFromLast("Parsed treees")

    trees.forEach(processCompilationUnit::accept)
    watch.lapFromLast("Processed trees")
    
//    val entered = JavacTaskUtil.enterTrees(task, trees)
//    watch.lapFromLast("Entered trees")
//
//    val analyzed = JavacTaskUtil.analyze(task, entered)
    task.analyze()
    watch.lapFromLast("Analyzed all trees")
  }
}

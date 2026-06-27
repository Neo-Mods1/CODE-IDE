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

import com.neo.ide.javac.services.compiler.ReusableContext
import com.neo.ide.javac.services.compiler.ReusableJavaCompiler
import com.neo.ide.lsp.java.parser.ts.TSJavaParser
import com.neo.ide.lsp.java.parser.ts.TSMethodPruner.prune
import com.neo.ide.projects.FileManager
import com.neo.ide.utils.VMUtils
import com.neo.ide.utils.withStopWatch
import jdkx.tools.JavaFileObject
import jdkx.tools.JavaFileObject.Kind.SOURCE
import openjdk.tools.javac.api.ClientCodeWrapper
import openjdk.tools.javac.tree.JCTree.JCCompilationUnit
import openjdk.tools.javac.util.Context
import kotlin.io.path.name

class JavaCompilerImpl(context: Context?) : ReusableJavaCompiler(context) {

  override fun parse(filename: JavaFileObject?, content: CharSequence?): JCCompilationUnit {

    if (VMUtils.isJvm()) {
      return super.parse(filename, content)
    }

    val file = ClientCodeWrapper.instance(context).unwrap(filename)
    val compilerConfig = JavaCompilerConfig.instance(context)

    // Preconditions
    if (
      content == null ||
        compilerConfig.files == null ||
        filename?.kind != SOURCE ||
        compilerConfig.files?.contains(file) == false
    ) {
      return super.parse(filename, content)
    }

    // If the file is NOT being parsed for a completion request,
    // we should not prune method bodies of active documents
    if (compilerConfig.completionInfo == null && FileManager.isActive(filename.toUri())) {
      return super.parse(filename, content)
    }

    val pruned = withStopWatch("${if(file is SourceFileObject) "[${file.path.name}] " else ""}Prune method bodies") { watch ->
      val contentBuilder = StringBuilder(content)

      return@withStopWatch TSJavaParser.parse(file).use { parseResult ->

        prune(
          contentBuilder,
          parseResult.tree,
          compilerConfig.completionInfo?.cursor?.index ?: -1
        )

        watch.log()

        return@use contentBuilder
      }
    }

    return super.parse(filename, pruned)
  }

  companion object {
    @JvmStatic
    fun preRegister(context: ReusableContext, replace: Boolean = false) {
      if (replace) {
        context.drop(compilerKey)
      }
      context.put(compilerKey, Context.Factory { JavaCompilerImpl(it) })
    }
  }
}

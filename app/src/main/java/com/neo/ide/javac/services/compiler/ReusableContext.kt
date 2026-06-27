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



package com.neo.ide.javac.services.compiler

import com.neo.ide.javac.services.CancelService
import com.neo.ide.javac.services.NBAttr
import com.neo.ide.javac.services.NBClassFinder
import com.neo.ide.javac.services.NBClassReader
import com.neo.ide.javac.services.NBEnter
import com.neo.ide.javac.services.NBJavacTrees
import com.neo.ide.javac.services.NBMemberEnter
import com.neo.ide.javac.services.NBParserFactory
import com.neo.ide.javac.services.NBResolve
import com.neo.ide.javac.services.NBTreeMaker
import com.neo.ide.javac.services.fs.CacheFSInfoSingleton
import com.neo.ide.javac.services.fs.JarPackageProviderImpl
import com.neo.ide.utils.VMUtils
import com.neo.ide.zipfs2.JarPackageProvider
import jdkx.tools.DiagnosticListener
import jdkx.tools.JavaFileManager
import jdkx.tools.JavaFileObject
import openjdk.source.util.JavacTask
import openjdk.source.util.TaskEvent
import openjdk.source.util.TaskEvent.Kind.ANALYZE
import openjdk.source.util.TaskListener
import openjdk.tools.javac.api.JavacTrees
import openjdk.tools.javac.api.MultiTaskListener
import openjdk.tools.javac.code.Types
import openjdk.tools.javac.comp.Annotate
import openjdk.tools.javac.comp.Check
import openjdk.tools.javac.comp.CompileStates
import openjdk.tools.javac.comp.Enter
import openjdk.tools.javac.comp.Modules
import openjdk.tools.javac.file.CacheFSInfo
import openjdk.tools.javac.file.FSInfo
import openjdk.tools.javac.main.Arguments
import openjdk.tools.javac.main.JavaCompiler
import openjdk.tools.javac.model.JavacElements
import openjdk.tools.javac.tree.JCTree.JCCompilationUnit
import openjdk.tools.javac.util.Context
import openjdk.tools.javac.util.DefinedBy
import openjdk.tools.javac.util.DefinedBy.Api.COMPILER_TREE
import openjdk.tools.javac.util.Log
import java.net.URI

/**
 * Reusable [Context] for [ReusableCompiler].
 *
 * @author Akash Yadav
 */
class ReusableContext(cancelService: CancelService) : Context(), TaskListener {
  
  private val flowCompleted = mutableSetOf<URI>()
  
  init {
    put(Log.logKey, ReusableLog.factory)
    put(FSInfo::class.java, if (VMUtils.isJvm()) CacheFSInfo() else CacheFSInfoSingleton)
    put(JavaCompiler.compilerKey, ReusableJavaCompiler.factory)
    put(JavacFlowListener.flowListenerKey, JavacFlowListener { this.hasFlowCompleted(it) })
    put(JarPackageProvider::class.java, JarPackageProviderImpl)
    
    NBAttr.preRegister(this)
    NBParserFactory.preRegister(this)
    NBTreeMaker.preRegister(this)
    NBJavacTrees.preRegister(this)
    NBResolve.preRegister(this)
    NBEnter.preRegister(this)
    NBMemberEnter.preRegister(this, false)
    NBClassFinder.preRegister(this)
    NBClassReader.preRegister(this)
    CancelService.preRegister(this, cancelService)
  }
  
  @DefinedBy(COMPILER_TREE)
  override fun started(e: TaskEvent) {
    //    log.debug("Started: $e")
    // Do nothing
  }
  
  @DefinedBy(COMPILER_TREE)
  override fun finished(e: TaskEvent) {
    if (e.kind == ANALYZE) {
      val cu = e.compilationUnit as JCCompilationUnit
      if (cu.sourcefile != null) {
        flowCompleted.add(cu.sourcefile.toUri())
      }
    }
  }
  
  fun clear() {
    drop(Arguments.argsKey)
    drop(DiagnosticListener::class.java)
    drop(Log.outKey)
    drop(Log.errKey)
    drop(JavaFileManager::class.java)
    drop(JavacTask::class.java)
    drop(JavacTrees::class.java)
    drop(JavacElements::class.java)
    
    if (ht[Log.logKey] is ReusableLog) {
      // log already init-ed - not first round
      (Log.instance(this) as ReusableLog).clear()
      Enter.instance(this).newRound()
      (JavaCompiler.instance(this) as ReusableJavaCompiler).clear()
      Types.instance(this).newRound()
      Check.instance(this).newRound()
      Modules.instance(this).newRound()
      Annotate.instance(this).newRound()
      CompileStates.instance(this).clear()
      MultiTaskListener.instance(this).clear()
    }
  }
  
  /** **FOR INTERNAL USE ONLY!** */
  fun <T> drop(k: Key<T>?) {
    ht.remove(k)
  }
  
  /** **FOR INTERNAL USE ONLY!** */
  fun <T> drop(c: Class<T>?) {
    drop(key(c))
  }
  
  private fun hasFlowCompleted(fo: JavaFileObject?): Boolean {
    return if (fo == null) {
      false
    } else {
      try {
        this.flowCompleted.contains(fo.toUri())
      } catch (e: Exception) {
        false
      }
    }
  }
}

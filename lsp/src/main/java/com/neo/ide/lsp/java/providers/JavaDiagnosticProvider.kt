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


package com.neo.ide.lsp.java.providers

import com.neo.ide.lsp.java.compiler.CompileTask
import com.neo.ide.lsp.java.compiler.JavaCompilerService
import com.neo.ide.lsp.java.providers.DiagnosticsProvider.findDiagnostics
import com.neo.ide.lsp.java.utils.CancelChecker
import com.neo.ide.lsp.models.DiagnosticResult
import com.neo.ide.progress.ProgressManager
import com.neo.ide.progress.ProgressManager.Companion.abortIfCancelled
import com.neo.ide.projects.FileManager
import com.neo.ide.projects.IProjectManager
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Code analyzer for java source code.
 *
 * @author Akash Yadav
 */
class JavaDiagnosticProvider {

  private val analyzeTimestamps = mutableMapOf<Path, Instant>()
  private var cachedDiagnostics = DiagnosticResult.NO_UPDATE
  private var analyzing = AtomicBoolean(false)
  private var analyzingThread: AnalyzingThread? = null

  companion object {

    private val log = LoggerFactory.getLogger(JavaDiagnosticProvider::class.java)
  }

  fun analyze(file: Path): DiagnosticResult {

    val module = IProjectManager.getInstance().getWorkspace()?.findModuleForFile(file, false)
      ?: return DiagnosticResult.NO_UPDATE
    val compiler = JavaCompilerService(module)

    abortIfCancelled()

    log.debug("Analyzing: {}", file)

    val modifiedAt = FileManager.getLastModified(file)
    val analyzedAt = analyzeTimestamps[file]

    if (analyzedAt?.isAfter(modifiedAt) == true) {
      log.debug("Using cached analyze results...")
      return cachedDiagnostics
    }

    analyzingThread?.let { analyzingThread ->
      if (analyzing.get()) {
        log.debug("Cancelling currently analyzing thread...")
        ProgressManager.instance.cancel(analyzingThread)
        this.analyzingThread = null
      }
    }

    analyzing.set(true)

    val analyzingThread = AnalyzingThread(compiler, file).also {
      analyzingThread = it
      it.start()
      it.join()
    }

    return analyzingThread.result.also {
      this.analyzingThread = null
    }
  }

  fun isAnalyzing(): Boolean {
    return this.analyzing.get()
  }

  fun cancel() {
    this.analyzingThread?.cancel()
  }

  fun clearTimestamp(file: Path) {
    analyzeTimestamps.remove(file)
  }

  private fun doAnalyze(file: Path, task: CompileTask): DiagnosticResult {
    val result =
      if (!isTaskValid(task)) {
        // Do not use Collections.emptyList ()
        // The returned list is accessed and the list returned by Collections.emptyList()
        // throws exception when trying to access.
        log.info("Using cached diagnostics")
        cachedDiagnostics
      } else
        DiagnosticResult(
          file,
          findDiagnostics(task, file).sortedBy {
            it.range
          }
        )
    return result.also {
      log.info("Analyze file completed. Found {} diagnostic items", result.diagnostics.size)
    }
  }

  private fun isTaskValid(task: CompileTask?): Boolean {
    abortIfCancelled()
    return task?.task != null && task.roots != null && task.roots.size > 0
  }

  inner class AnalyzingThread(val compiler: JavaCompilerService, val file: Path) :
    Thread("JavaAnalyzerThread") {

    var result: DiagnosticResult = DiagnosticResult.NO_UPDATE

    fun cancel() {
      ProgressManager.instance.cancel(this)
    }

    override fun run() {
      result =
        try {
          compiler.compile(file).get { task -> doAnalyze(file, task) }
        } catch (err: Throwable) {
          if (CancelChecker.isCancelled(err)) {
            log.error("Analyze request cancelled")
          } else {
            log.warn("Unable to analyze file", err)
          }
          DiagnosticResult.NO_UPDATE
        } finally {
          compiler.destroy()
          analyzing.set(false)
        }
          .also {
            cachedDiagnostics = it
            analyzeTimestamps[file] = Instant.now()
          }
    }
  }
}

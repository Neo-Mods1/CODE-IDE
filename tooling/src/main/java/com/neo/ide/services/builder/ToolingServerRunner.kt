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



package com.neo.ide.services.builder

import ch.qos.logback.core.CoreConstants
import com.neo.ide.shell.executeProcessAsync
import com.neo.ide.tasks.cancelIfActive
import com.neo.ide.tasks.ifCancelledOrInterrupted
import com.neo.ide.tooling.api.IProject
import com.neo.ide.tooling.api.IToolingApiClient
import com.neo.ide.tooling.api.IToolingApiServer
import com.neo.ide.tooling.api.util.ToolingApiLauncher
import com.neo.ide.utils.Environment
import com.termux.shared.reflection.ReflectionUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Runner thread for the Tooling API.
 *
 * @author Akash Yadav
 */
internal class ToolingServerRunner(
  private var listener: OnServerStartListener?,
  private var observer: Observer?,
) {

  internal var pid: Int? = null
  private var _job: Job? = null
  private var _isStarted = AtomicBoolean(false)

  var isStarted: Boolean
    get() = _isStarted.get()
    private set(value) {
      _isStarted.set(value)
    }

  private val runnerScope = CoroutineScope(Dispatchers.IO + CoroutineName("ToolingServerRunner"))

  companion object {

    private val log = LoggerFactory.getLogger(ToolingServerRunner::class.java)
  }

  fun setListener(listener: OnServerStartListener?) {
    this.listener = listener
  }

  fun startAsync(envs: Map<String, String>) = runnerScope.launch {
    var process: Process?
    try {
      log.info("Starting tooling API server...")
      val command = listOf(
        Environment.JAVA.absolutePath, // The 'java' binary executable
        // Allow reflective access to private members of classes in the following
        // packages:
        // - java.lang
        // - java.io
        // - java.util
        //
        // If any of the model classes in 'tooling-api-model' module send/receive
        // objects from the JDK, their package name must be declared here with
        // '--add-opens' to prevent InaccessibleObjectException.
        // For example, some of the model classes has members of type java.io.File.
        // When sending/receiving these type of objects using LSP4J, members of
        // these objects are reflectively accessed by Gson. If we do no specify
        // '--add-opens' for 'java.io' (for java.io.File) package, JVM will throw an
        // InaccessibleObjectException.
        "--add-opens", "java.base/java.lang=ALL-UNNAMED", "--add-opens",
        "java.base/java.util=ALL-UNNAMED", "--add-opens",
        "java.base/java.io=ALL-UNNAMED", // The JAR file to run
        "-D${CoreConstants.STATUS_LISTENER_CLASS_KEY}=com.neo.ide.tooling.impl.util.LogbackStatusListener",
        "-jar", Environment.TOOLING_API_JAR.absolutePath
      )

      process = executeProcessAsync {
        this.command = command

        // input and output is used for communication to the tooling server
        // error stream is used to read the server logs
        this.redirectErrorStream = false
        this.workingDirectory = null // HOME
        this.environment = envs
      }

      pid = ReflectionUtils.getDeclaredField(process::class.java, "pid")?.get(process) as Int?
      pid ?: throw IllegalStateException("Unable to get process ID")

      val inputStream = process.inputStream
      val outputStream = process.outputStream
      val errorStream = process.errorStream

      val processJob = launch(Dispatchers.IO) {
        try {
          process?.waitFor()
          log.info("Tooling API process exited with code : {}", process?.exitValue() ?: "<unknown>")
          process = null
        } finally {
          log.info("Destroying Tooling API process...")
          process?.destroyForcibly()
        }
      }

      val launcher = ToolingApiLauncher.newClientLauncher(
        observer!!.getClient(),
        inputStream,
        outputStream
      )

      val future = launcher.startListening()
      observer?.onListenerStarted(
        server = launcher.remoteProxy as IToolingApiServer,
        projectProxy = launcher.remoteProxy as IProject,
        errorStream = errorStream
      )

      isStarted = true

      listener?.onServerStarted(pid!!)

      // we don't need the listener anymore
      // also, this might be a reference to the activity
      // release to prevent memory leak
      listener = null

      // Wait(block) until the process terminates
      val serverJob = launch(Dispatchers.IO) {
        try {
          future.get()
        } catch (err: Throwable) {
          err.ifCancelledOrInterrupted {
            log.info("ToolingServerThread has been cancelled or interrupted.")
          }

          // rethrow the error
          throw err
        }
      }

      processJob.join()
      joinAll(serverJob, processJob)
    } catch (e: Throwable) {
      if (e !is CancellationException) {
        log.error("Unable to start tooling API server", e)
      }
    }
  }.also {
    _job = it
  }

  fun release() {
    this.listener = null
    this.observer = null
    this._job?.cancel(CancellationException("Cancellation was requested"))
    this.runnerScope.cancelIfActive("Cancellation was requested")
  }

  interface Observer {

    fun onListenerStarted(
      server: IToolingApiServer,
      projectProxy: IProject,
      errorStream: InputStream,
    )

    fun onServerExited(exitCode: Int)

    fun getClient(): IToolingApiClient
  }

  /** Callback to listen for Tooling API server start event.  */
  fun interface OnServerStartListener {

    /** Called when the tooling API server has been successfully started.  */
    fun onServerStarted(pid: Int)
  }
}

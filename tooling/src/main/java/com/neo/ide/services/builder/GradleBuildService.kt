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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.neo.ide.app.BaseApplication
import com.neo.ide.lookup.Lookup
import com.neo.ide.projects.internal.ProjectManagerImpl
import com.neo.ide.projects.builder.BuildService
import com.neo.ide.tooling.api.ForwardingToolingApiClient
import com.neo.ide.tooling.api.IProject
import com.neo.ide.tooling.api.IToolingApiClient
import com.neo.ide.tooling.api.IToolingApiServer
import com.neo.ide.tooling.api.messages.InitializeProjectParams
import com.neo.ide.tooling.api.messages.result.BuildCancellationRequestResult
import com.neo.ide.tooling.api.messages.result.BuildInfo
import com.neo.ide.tooling.api.messages.result.InitializeResult
import com.neo.ide.tooling.api.messages.result.TaskExecutionResult
import com.neo.ide.tooling.api.models.ToolingServerMetadata
import com.neo.ide.tooling.events.ProgressEvent
import com.neo.ide.tasks.runOnUiThread
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

class GradleBuildService : Service(), BuildService, IToolingApiClient,
  ToolingServerRunner.Observer {

  private var _toolingApiClient: ForwardingToolingApiClient? = null
  private var toolingServerRunner: ToolingServerRunner? = null
  private var eventListener: EventListener? = null
  private var server: IToolingApiServer? = null

  override var isBuildInProgress = false
    private set

  override fun isToolingServerStarted() = toolingServerRunner?.isStarted == true

  override fun metadata(): CompletableFuture<ToolingServerMetadata> {
    return CompletableFuture.completedFuture(ToolingServerMetadata())
  }

  override fun initializeProject(params: InitializeProjectParams): CompletableFuture<InitializeResult> {
    return CompletableFuture.completedFuture(InitializeResult())
  }

  override fun executeTasks(vararg tasks: String): CompletableFuture<TaskExecutionResult> {
    return CompletableFuture.completedFuture(TaskExecutionResult())
  }

  override fun cancelCurrentBuild(): CompletableFuture<BuildCancellationRequestResult> {
    return CompletableFuture.completedFuture(BuildCancellationRequestResult())
  }

  override fun onBind(intent: Intent): IBinder? {
    if (mBinder == null) {
      mBinder = GradleServiceBinder(this)
    }
    return mBinder
  }

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
    Lookup.getDefault().register(BuildService.KEY_BUILD_SERVICE, this)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    startForeground(NOTIFICATION_ID, createNotification("Building..."))
    return START_NOT_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    Lookup.getDefault().unregister(BuildService.KEY_BUILD_SERVICE)
  }

  // IToolingApiClient implementation
  override fun prepareBuild(buildInfo: BuildInfo) {
    eventListener?.prepareBuild(buildInfo)
  }

  override fun onBuildSuccessful(tasks: List<String?>) {
    eventListener?.onBuildSuccessful(tasks)
    isBuildInProgress = false
    Lookup.getDefault().unregister(BuildService.KEY_PROJECT_PROXY)
    stopForeground(STOP_FOREGROUND_REMOVE)
    stopSelf()
  }

  override fun onProgressEvent(event: ProgressEvent) {
    eventListener?.onProgressEvent(event)
  }

  override fun onBuildFailed(tasks: List<String?>) {
    eventListener?.onBuildFailed(tasks)
    isBuildInProgress = false
    Lookup.getDefault().unregister(BuildService.KEY_PROJECT_PROXY)
    stopForeground(STOP_FOREGROUND_REMOVE)
    stopSelf()
  }

  override fun onOutput(line: String?) {
    eventListener?.onOutput(line)
  }

  override fun onServerStarted(pid: Int) {
    log.info("Tooling API server started with pid: {}", pid)
  }

  override fun onServerStartError(error: Throwable) {
    log.error("Failed to start tooling API server", error)
  }

  fun setEventListener(eventListener: EventListener?): GradleBuildService {
    this.eventListener = eventListener?.let { wrap(it) }
    return this
  }

  private fun wrap(listener: EventListener): EventListener {
    return object : EventListener {
      override fun prepareBuild(buildInfo: BuildInfo) {
        runOnUiThread { listener.prepareBuild(buildInfo) }
      }
      override fun onBuildSuccessful(tasks: List<String?>) {
        runOnUiThread { listener.onBuildSuccessful(tasks) }
      }
      override fun onProgressEvent(event: ProgressEvent) {
        runOnUiThread { listener.onProgressEvent(event) }
      }
      override fun onBuildFailed(tasks: List<String?>) {
        runOnUiThread { listener.onBuildFailed(tasks) }
      }
      override fun onOutput(line: String?) {
        runOnUiThread { listener.onOutput(line) }
      }
    }
  }

  private fun createNotificationChannel() {
    val channel = NotificationChannel(
      CHANNEL_ID,
      "Build Service",
      NotificationManager.IMPORTANCE_LOW
    )
    val manager = getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(channel)
  }

  private fun createNotification(contentText: String): Notification {
    return Notification.Builder(this, CHANNEL_ID)
      .setContentTitle("CODE-IDE")
      .setContentText(contentText)
      .setSmallIcon(android.R.drawable.ic_menu_send)
      .setOngoing(true)
      .build()
  }

  interface EventListener {
    fun prepareBuild(buildInfo: BuildInfo)
    fun onBuildSuccessful(tasks: List<String?>)
    fun onProgressEvent(event: ProgressEvent)
    fun onBuildFailed(tasks: List<String?>)
    fun onOutput(line: String?)
  }

  companion object {
    private const val CHANNEL_ID = "build_service"
    private const val NOTIFICATION_ID = 1
    private val log = LoggerFactory.getLogger(GradleBuildService::class.java)
  }
}

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



package com.neo.ide.fragments.output

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.neo.ide.R
import com.neo.ide.preferences.internal.DevOpsPreferences
import com.neo.ide.services.log.ConnectionObserverParams
import com.neo.ide.services.log.LogReceiverImpl
import com.neo.ide.services.log.LogReceiverService
import com.neo.ide.services.log.LogReceiverServiceConnection
import com.neo.ide.services.log.lookupLogService
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Fragment to show application logs.
 * @author Akash Yadav
 */
class AppLogFragment : LogViewFragment() {

  private val isBoundToLogReceiver = AtomicBoolean(false)

  private var logServiceConnection: LogReceiverServiceConnection? = null
  private var logReceiverImpl: LogReceiverImpl? = null

  private val logServiceConnectionObserver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if (intent?.action != LogReceiverService.ACTION_CONNECTION_UPDATE) {
        log.warn(
          "Received invalid broadcast. Action '${LogReceiverService.ACTION_CONNECTION_UPDATE}' is expected.")
        return
      }

      val params = ConnectionObserverParams.from(intent) ?: run {
        log.warn(
          "Received ${LogReceiverService.ACTION_CONNECTION_UPDATE} broadcast, but invalid extras were provided: $intent")
        return
      }

      val isBound = isBoundToLogReceiver.get()
      if (!isBound && params.totalConnections > 0) {
        // log receiver has been connected to one or more log senders
        // bind to the receiver and notify senders to start reading logs
        bindToLogReceiver()
        return
      }

      if (isBound && params.totalConnections == 0) {
        // all log senders have been disconnected from the log receiver
        // unbind from the log receiver
        unbindFromLogReceiver()
        return
      }
    }
  }

  companion object {

    private val log = LoggerFactory.getLogger(AppLogFragment::class.java)
  }

  override fun isSimpleFormattingEnabled() = false
  override fun getFilename() = "app_logs"

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    emptyStateViewModel.emptyMessage.value = if (DevOpsPreferences.logsenderEnabled) {
      getString(R.string.msg_emptyview_applogs)
    } else {
      getString(R.string.msg_logsender_disabled)
    }

    registerLogConnectionObserver()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    unregisterLogConnectionObserver()

    if (isBoundToLogReceiver.get()) {
      unbindFromLogReceiver()
    }
  }

  private fun registerLogConnectionObserver() {
    try {
      val intentFilter = IntentFilter(LogReceiverService.ACTION_CONNECTION_UPDATE)
      LocalBroadcastManager.getInstance(requireContext())
        .registerReceiver(logServiceConnectionObserver, intentFilter)
    } catch (e: Exception) {
      log.warn("Failed to register connection observer for LogReceiverService", e)
    }
  }

  private fun unregisterLogConnectionObserver() {
    try {
      LocalBroadcastManager.getInstance(requireContext())
        .unregisterReceiver(logServiceConnectionObserver)
    } catch (e: Exception) {
      log.warn("Failed to unregister connection observer for LogReceiverService", e)
    }
  }

  private fun bindToLogReceiver() {
    try {
      if (!DevOpsPreferences.logsenderEnabled) {
        log.info("LogSender is disabled. LogReceiver service won't be started...")

        // release the connection listener
        logServiceConnection?.onConnected = null
        return
      }

      val context = context ?: return
      val intent = Intent(context, LogReceiverService::class.java).setAction(
        LogReceiverService.ACTION_CONNECT_LOG_CONSUMER)

      val serviceConnection = logServiceConnection ?: LogReceiverServiceConnection { binder ->
        logReceiverImpl = binder
        lookupLogService()?.setConsumer(this::appendLog)
      }.also { serviceConnection ->
        logServiceConnection = serviceConnection
      }

      // do not auto create the service with BIND_AUTO_CREATE
      check(context.bindService(intent, serviceConnection, Context.BIND_IMPORTANT))
      this.isBoundToLogReceiver.set(true)
      log.info("LogReceiver service is being started")
    } catch (err: Throwable) {
      log.error("Failed to start LogReceiver service", err)
    }
  }

  private fun unbindFromLogReceiver() {
    try {
      if (!DevOpsPreferences.logsenderEnabled) {
        return
      }

      lookupLogService()?.setConsumer(null)
      logReceiverImpl?.disconnectAll()

      val serviceConnection = logServiceConnection ?: run {
        log.warn("Trying to unbind from LogReceiverService, but ServiceConnection is null")
        return
      }

      val context = context ?: return
      context.unbindService(serviceConnection)

      this.isBoundToLogReceiver.set(false)
      log.info("Unbound from LogReceiver service")
    } catch (e: Exception) {
      log.error("Failed to unbind from LogReceiver service")
    } finally {
      this.logServiceConnection?.onConnected = null
      this.logServiceConnection = null

      this.logReceiverImpl = null
    }
  }
}
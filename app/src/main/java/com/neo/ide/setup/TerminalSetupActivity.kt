package com.neo.ide.setup

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.neo.ide.R
import com.neo.ide.download.ResourceManager
import com.neo.ide.download.SetupState
import com.neo.ide.activities.MainActivity
import android.content.Intent
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import com.termux.view.TerminalView
import com.termux.view.TerminalViewClient
import kotlinx.coroutines.*
import org.json.JSONArray
import java.io.File
import android.view.KeyEvent

class TerminalSetupActivity : AppCompatActivity(), TerminalSessionClient {

    private lateinit var terminalView: TerminalView
    private lateinit var statusText: TextView
    private lateinit var progressBar: ProgressBar
    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val resourceManager by lazy { ResourceManager(this) }

    private var terminalSession: TerminalSession? = null
    private val pendingOutput = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminal_setup)

        terminalView = findViewById(R.id.terminal_view)
        statusText = findViewById(R.id.setup_status_text)
        progressBar = findViewById(R.id.setup_progress)

        terminalView.setTerminalViewClient(object : TerminalViewClient {
            override fun onScale(scale: Float): Float = 1.0f
            override fun onSingleTapUp(e: MotionEvent) {}
            override fun shouldBackButtonBeMappedToEscape(): Boolean = false
            override fun shouldEnforceCharBasedInput(): Boolean = false
            override fun shouldUseCtrlSpaceWorkaround(): Boolean = false
            override fun isTerminalViewSelected(): Boolean = true
            override fun copyModeChanged(copyMode: Boolean) {}
            override fun onKeyDown(keyCode: Int, e: KeyEvent, session: TerminalSession?): Boolean = false
            override fun onKeyUp(keyCode: Int, e: KeyEvent): Boolean = false
            override fun onLongPress(event: MotionEvent): Boolean = false
            override fun readControlKey(): Boolean = false
            override fun readAltKey(): Boolean = false
            override fun readShiftKey(): Boolean = false
            override fun readFnKey(): Boolean = false
            override fun onCodePoint(codePoint: Int, ctrlDown: Boolean, session: TerminalSession?): Boolean = false
            override fun onEmulatorSet() {
                flushPendingOutput()
            }
            override fun logError(tag: String, message: String) {}
            override fun logWarn(tag: String, message: String) {}
            override fun logInfo(tag: String, message: String) {}
            override fun logDebug(tag: String, message: String) {}
            override fun logVerbose(tag: String, message: String) {}
            override fun logStackTraceWithMessage(tag: String, message: String, e: Exception?) {}
            override fun logStackTrace(tag: String, e: Exception?) {}
        })

        val shell = getShellPath()
        val cwd = filesDir.absolutePath
        terminalSession = TerminalSession(shell, cwd, arrayOf(shell), null, null, this)
        terminalSession?.mSessionName = "setup"
        terminalView.setTextSize(10)
        terminalView.attachSession(terminalSession)

        val selectedResourcesJson = intent.getStringExtra("selected_resources")

        scope.launch {
            delay(300)
            if (selectedResourcesJson != null) {
                runSetupWithSelection(selectedResourcesJson)
            } else {
                runSetupLegacy()
            }
        }
    }

    private fun flushPendingOutput() {
        if (pendingOutput.isEmpty()) return
        val session = terminalSession ?: return
        for (text in pendingOutput) {
            session.writeToTerminal(text)
        }
        pendingOutput.clear()
    }

    private fun writeToTerminal(text: String) {
        val session = terminalSession
        if (session != null && session.emulator != null) {
            session.writeToTerminal(text)
        } else {
            pendingOutput.add(text)
        }
    }

    private fun getShellPath(): String {
        val shells = listOf("/data/data/com.termux/files/usr/bin/bash", "/system/bin/sh")
        return shells.firstOrNull { File(it).exists() } ?: "/system/bin/sh"
    }

    private fun runSetupWithSelection(jsonStr: String) {
        val resourcesArray = JSONArray(jsonStr)
        if (resourcesArray.length() == 0) {
            writeToTerminal("\r\n\u001B[33mNo resources selected.\u001B[0m\r\n")
            finishSetup()
            return
        }

        writeToTerminal("\r\n")
        writeToTerminal("\u001B[1;36m\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u001B[0m\r\n")
        writeToTerminal("\u001B[1;36m         CODE-IDE Setup               \u001B[0m\r\n")
        writeToTerminal("\u001B[1;36m\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u001B[0m\r\n\r\n")
        writeToTerminal("\u001B[1mInstalling ${resourcesArray.length()} selected resources:\u001B[0m\r\n")

        for (i in 0 until resourcesArray.length()) {
            val obj = resourcesArray.getJSONObject(i)
            val name = obj.getString("name")
            val version = obj.optString("version", "")
            val sizeBytes = obj.optLong("size", 0)
            val sizeMB = sizeBytes / (1024.0 * 1024.0)
            writeToTerminal("  \u001B[32m${i + 1}.\u001B[0m $name v$version (${String.format("%.1f", sizeMB)} MB)\r\n")
        }
        writeToTerminal("\r\n")

        val entries = mutableListOf<ResourceManager.ResourceEntry>()
        for (i in 0 until resourcesArray.length()) {
            val obj = resourcesArray.getJSONObject(i)
            entries.add(
                ResourceManager.ResourceEntry(
                    name = obj.getString("name"),
                    category = obj.getString("category"),
                    version = obj.optString("version", ""),
                    size = obj.optLong("size", 0),
                    sha256 = obj.optString("sha256", ""),
                    format = obj.optString("format", "tar.xz"),
                    url = obj.getString("url"),
                    destination = obj.optString("destination", "{install_dir}/${obj.getString("name")}")
                )
            )
        }

        val toInstall = entries.filter { !resourceManager.isResourceInstalled(it) }

        if (toInstall.isEmpty()) {
            writeToTerminal("\u001B[32mAll selected resources already installed.\u001B[0m\r\n")
            finishSetup()
            return
        }

        writeToTerminal("\u001B[33mNeed to download ${toInstall.size} resources.\u001B[0m\r\n\r\n")

        handler.post { progressBar.visibility = View.VISIBLE }

        scope.launch {
            val success = withContext(Dispatchers.IO) {
                resourceManager.downloadResources(
                    toInstall,
                    onResourceStart = { resource, current, total ->
                        writeToTerminal("\u001B[1m[$current/$total]\u001B[0m Downloading ${resource.name}...\r\n")
                    },
                    onResourceProgress = { resource, progress ->
                        val pct = (progress * 100).toInt()
                        handler.post {
                            statusText.text = "Downloading ${resource.name}... $pct%"
                        }
                    },
                    onResourceComplete = { resource ->
                        writeToTerminal("  \u001B[32m\u2713\u001B[0m Downloaded ${resource.name}\r\n")
                    },
                    onError = { error ->
                        writeToTerminal("\u001B[31mERROR: $error\u001B[0m\r\n")
                    }
                )
            }

            if (!success) {
                writeToTerminal("\r\n\u001B[31mDownload failed. Check your connection and try again.\u001B[0m\r\n")
                updateStatus("Failed")
                handler.post { progressBar.visibility = View.GONE }
                return@launch
            }

            writeToTerminal("\r\n\u001B[1mDownloads complete. Extracting...\u001B[0m\r\n\r\n")

            withContext(Dispatchers.IO) {
                for (resource in toInstall) {
                    writeToTerminal("Extracting ${resource.name}...\r\n")
                    val result = resourceManager.extractResource(resource, object : ResourceManager.ExtractionListener {
                        override fun onExtractionStart(fileName: String) {}
                        override fun onExtractionComplete(destDir: File) {
                            writeToTerminal("  \u001B[32m\u2713\u001B[0m Extracted to ${destDir.name}\r\n")
                        }
                        override fun onExtractionError(error: String) {
                            writeToTerminal("  \u001B[31m\u2717\u001B[0m $error\r\n")
                        }
                    })
                    if (result.isFailure) {
                        writeToTerminal("  \u001B[31mFailed to extract ${resource.name}: ${result.exceptionOrNull()?.message}\u001B[0m\r\n")
                    }
                }
            }

            writeToTerminal("\r\n\u001B[1;32mSetup complete!\u001B[0m\r\n")
            finishSetup()
        }
    }

    private fun runSetupLegacy() {
        writeToTerminal("\r\n")
        writeToTerminal("\u001B[1;36m\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u001B[0m\r\n")
        writeToTerminal("\u001B[1;36m         CODE-IDE Setup               \u001B[0m\r\n")
        writeToTerminal("\u001B[1;36m\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u001B[0m\r\n\r\n")
        writeToTerminal("\u001B[1mFetching resource manifest...\u001B[0m\r\n")

        scope.launch {
            val manifestResult = withContext(Dispatchers.IO) { resourceManager.fetchManifest() }
            if (manifestResult.isFailure) {
                writeToTerminal("\u001B[31mERROR: Failed to fetch manifest: ${manifestResult.exceptionOrNull()?.message}\u001B[0m\r\n")
                writeToTerminal("\u001B[33mCheck your internet connection and try again.\u001B[0m\r\n")
                updateStatus("Failed")
                handler.post { progressBar.visibility = View.GONE }
                return@launch
            }

            val manifest = manifestResult.getOrThrow()
            writeToTerminal("Manifest v${manifest.version} (${manifest.resources.size} resources)\r\n\r\n")

            val requiredResources = manifest.resources.filter { !resourceManager.isResourceInstalled(it) }

            if (requiredResources.isEmpty()) {
                writeToTerminal("\u001B[32mAll resources already installed.\u001B[0m\r\n")
                finishSetup()
                return@launch
            }

            writeToTerminal("\u001B[1mNeed to download ${requiredResources.size} resources:\u001B[0m\r\n")
            requiredResources.forEach { res ->
                val sizeMB = res.size / (1024.0 * 1024.0)
                writeToTerminal("  \u001B[32m-\u001B[0m ${res.name} (${String.format("%.1f", sizeMB)} MB)\r\n")
            }
            writeToTerminal("\r\n")

            handler.post { progressBar.visibility = View.VISIBLE }

            val success = withContext(Dispatchers.IO) {
                resourceManager.downloadResources(
                    requiredResources,
                    onResourceStart = { resource, current, total ->
                        writeToTerminal("\u001B[1m[$current/$total]\u001B[0m Downloading ${resource.name}...\r\n")
                    },
                    onResourceProgress = { resource, progress ->
                        val pct = (progress * 100).toInt()
                        handler.post {
                            statusText.text = "Downloading ${resource.name}... $pct%"
                        }
                    },
                    onResourceComplete = { resource ->
                        writeToTerminal("  \u001B[32m\u2713\u001B[0m Downloaded ${resource.name}\r\n")
                    },
                    onError = { error ->
                        writeToTerminal("\u001B[31mERROR: $error\u001B[0m\r\n")
                    }
                )
            }

            if (!success) {
                writeToTerminal("\r\n\u001B[31mDownload failed. Please check your connection and try again.\u001B[0m\r\n")
                updateStatus("Failed")
                handler.post { progressBar.visibility = View.GONE }
                return@launch
            }

            writeToTerminal("\r\n\u001B[1mAll downloads complete. Extracting...\u001B[0m\r\n")

            withContext(Dispatchers.IO) {
                for (resource in requiredResources) {
                    writeToTerminal("Extracting ${resource.name}...\r\n")
                    val result = resourceManager.extractResource(resource, object : ResourceManager.ExtractionListener {
                        override fun onExtractionStart(fileName: String) {}
                        override fun onExtractionComplete(destDir: File) {
                            writeToTerminal("  \u001B[32m\u2713\u001B[0m Extracted to ${destDir.name}\r\n")
                        }
                        override fun onExtractionError(error: String) {
                            writeToTerminal("  \u001B[31m\u2717\u001B[0m $error\r\n")
                        }
                    })
                    if (result.isFailure) {
                        writeToTerminal("  \u001B[31mFailed to extract ${resource.name}: ${result.exceptionOrNull()?.message}\u001B[0m\r\n")
                    }
                }
            }

            resourceManager.setInstalledVersion(manifest.version)
            writeToTerminal("\r\n\u001B[1;32mSetup complete!\u001B[0m\r\n")
            finishSetup()
        }
    }

    private fun finishSetup() {
        SetupState.setSetupComplete(this, true)
        updateStatus("Complete")
        handler.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)
    }

    private fun updateStatus(text: String) {
        handler.post {
            statusText.text = text
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        terminalSession?.finishIfRunning()
    }

    override fun onTextChanged(changedSession: TerminalSession) {
        if (changedSession === terminalSession) {
            handler.post { terminalView.onScreenUpdated() }
        }
    }

    override fun onTitleChanged(changedSession: TerminalSession) {}
    override fun onSessionFinished(finishedSession: TerminalSession) {}
    override fun onCopyTextToClipboard(session: TerminalSession, text: String) {}
    override fun onPasteTextFromClipboard(session: TerminalSession?) {}
    override fun onBell(session: TerminalSession) {}
    override fun onColorsChanged(changedSession: TerminalSession) {}
    override fun onTerminalCursorStateChange(state: Boolean) {}
    override fun setTerminalShellPid(session: TerminalSession, pid: Int) {}
    override fun getTerminalCursorStyle(): Int? = null
    override fun logError(tag: String, message: String) {}
    override fun logWarn(tag: String, message: String) {}
    override fun logInfo(tag: String, message: String) {}
    override fun logDebug(tag: String, message: String) {}
    override fun logVerbose(tag: String, message: String) {}
    override fun logStackTraceWithMessage(tag: String, message: String, e: Exception?) {}
    override fun logStackTrace(tag: String, e: Exception?) {}
}

/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.setup

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.neo.ide.R
import com.neo.ide.download.ResourceManager
import com.neo.ide.download.SetupState
import com.neo.ide.activities.HomeActivity
import android.content.Intent
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import com.termux.view.TerminalView
import com.termux.view.TerminalViewClient
import kotlinx.coroutines.*
import org.json.JSONArray
import java.io.File

class TerminalSetupActivity : AppCompatActivity(), TerminalSessionClient {

    private lateinit var terminalView: TerminalView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sessionListView: ListView
    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val resourceManager by lazy { ResourceManager(this) }

    private val sessions = mutableListOf<TerminalSession>()
    private var currentSession: TerminalSession? = null
    private val pendingOutput = mutableMapOf<Int, MutableList<String>>()
    private var currentFontSize = 0
    private var sessionCounter = 0
    private var setupCompleted = false

    private lateinit var sessionAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminal_setup)

        window.decorView.setBackgroundColor(Color.BLACK)
        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK

        terminalView = findViewById(R.id.terminal_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        sessionListView = findViewById(R.id.terminal_sessions_list)
        val extraKeys = findViewById<ExtraKeysView>(R.id.extra_keys_view)
        extraKeys.setTerminalView(terminalView)

        // AndroidIDE default: 12dp font size
        currentFontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics).toInt()

        // Toggle keyboard button
        findViewById<TextView>(R.id.toggle_keyboard_button).setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.toggleSoftInput(0, 0)
        }

        // Session list adapter — matches AndroidIDE style
        sessionAdapter = object : ArrayAdapter<String>(this, R.layout.item_terminal_session) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.item_terminal_session, parent, false)
                val session = sessions.getOrNull(position)
                val titleText = view.findViewById<TextView>(R.id.session_title)

                if (session != null) {
                    val num = position + 1
                    val name = session.mSessionName ?: "shell"
                    val cwd = session.cwd?.substringAfterLast('/') ?: ""
                    titleText.text = "[$num] $name\n    $cwd"

                    if (session == currentSession) {
                        titleText.setTextColor(Color.parseColor("#FF4CAF50"))
                    } else if (session.exitStatus != 0) {
                        titleText.setTextColor(Color.parseColor("#FFEF5350"))
                    } else {
                        titleText.setTextColor(Color.parseColor("#FFDDDDDD"))
                    }

                    view.setOnClickListener {
                        switchToSession(session)
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                }
                return view
            }
        }
        sessionListView.adapter = sessionAdapter

        // New session button
        findViewById<TextView>(R.id.new_session_button).setOnClickListener {
            createNewSession()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // Terminal view client — matches AndroidIDE configuration
        terminalView.setTerminalViewClient(object : TerminalViewClient {
            override fun onScale(scale: Float): Float {
                if (scale < 0.9f || scale > 1.1f) {
                    changeFontSize(scale > 1f)
                    return 1.0f
                }
                return scale
            }
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
                val session = currentSession ?: return
                session.emulator?.let { em ->
                    window.decorView.setBackgroundColor(em.mColors.mCurrentColors[257])
                }
                val idx = sessions.indexOf(session)
                if (idx >= 0) {
                    val pending = pendingOutput.remove(idx)
                    pending?.forEach { session.writeToTerminal(it) }
                }
            }
            override fun logError(tag: String, message: String) {}
            override fun logWarn(tag: String, message: String) {}
            override fun logInfo(tag: String, message: String) {}
            override fun logDebug(tag: String, message: String) {}
            override fun logVerbose(tag: String, message: String) {}
            override fun logStackTraceWithMessage(tag: String, message: String, e: Exception?) {}
            override fun logStackTrace(tag: String, e: Exception?) {}
        })

        // Start foreground service
        TerminalService.start(this)

        // Create first session and print welcome text
        createNewSession()

        // Print initial welcome banner (like AndroidIDE shows before setup)
        val selectedResourcesJson = intent.getStringExtra("selected_resources")

        scope.launch {
            delay(300)
            // Print welcome banner
            writeToTerminal("\r\n")
            writeToTerminal("\u001B[1;36m╔══════════════════════════════════════╗\u001B[0m\r\n")
            writeToTerminal("\u001B[1;36m║         CODE-IDE Setup               ║\u001B[0m\r\n")
            writeToTerminal("\u001B[1;36m╚══════════════════════════════════════╝\u001B[0m\r\n")
            writeToTerminal("\r\n")
            writeToTerminal("\u001B[33mWelcome to CODE-IDE!\u001B[0m\r\n")
            writeToTerminal("\u001B[37mSetting up your development environment...\u001B[0m\r\n")
            writeToTerminal("\r\n")

            delay(200)

            if (selectedResourcesJson != null) {
                runSetupWithSelection(selectedResourcesJson)
            } else {
                runSetupLegacy()
            }
        }
    }

    private fun createNewSession(): TerminalSession {
        sessionCounter++
        val shell = getShellPath()
        val homeDir = File(filesDir, "home")
        if (!homeDir.exists()) homeDir.mkdirs()
        val cwd = homeDir.absolutePath
        val session = TerminalSession(shell, cwd, arrayOf(shell), null, null, this)
        session.mSessionName = "session_$sessionCounter"
        sessions.add(session)
        sessionAdapter.notifyDataSetChanged()

        if (currentSession == null) {
            switchToSession(session)
        }
        return session
    }

    private fun switchToSession(session: TerminalSession) {
        if (session == currentSession) return
        currentSession = session
        terminalView.setTextSize(currentFontSize)
        terminalView.attachSession(session)
        sessionAdapter.notifyDataSetChanged()
    }

    private fun removeSession(session: TerminalSession) {
        if (sessions.size <= 1) {
            Toast.makeText(this, "Can't close last session", Toast.LENGTH_SHORT).show()
            return
        }
        session.finishIfRunning()
        sessions.remove(session)
        if (currentSession == session) {
            currentSession = null
            switchToSession(sessions.first())
        }
        sessionAdapter.notifyDataSetChanged()
    }

    private fun writeToTerminal(text: String) {
        val session = currentSession ?: return
        val idx = sessions.indexOf(session)
        if (session.emulator != null) {
            session.writeToTerminal(text)
        } else {
            pendingOutput.getOrPut(idx) { mutableListOf() }.add(text)
        }
    }

    private fun changeFontSize(increase: Boolean) {
        val delta = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 2f, resources.displayMetrics).toInt()
        currentFontSize = if (increase) currentFontSize + delta else currentFontSize - delta
        val minSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 4f, resources.displayMetrics).toInt()
        val maxSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 256f, resources.displayMetrics).toInt()
        currentFontSize = currentFontSize.coerceIn(minSize, maxSize)
        terminalView.setTextSize(currentFontSize)
    }

    private fun getShellPath(): String {
        val shells = listOf("/data/data/com.termux/files/usr/bin/bash", "/system/bin/sh")
        return shells.firstOrNull { File(it).exists() } ?: "/system/bin/sh"
    }

    private fun runSetupWithSelection(jsonStr: String) {
        val resourcesArray = JSONArray(jsonStr)
        if (resourcesArray.length() == 0) {
            writeToTerminal("\u001B[33mNo resources selected.\u001B[0m\r\n")
            finishSetup()
            return
        }

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

        scope.launch {
            val success = withContext(Dispatchers.IO) {
                resourceManager.downloadResources(
                    toInstall,
                    onResourceStart = { resource, current, total ->
                        writeToTerminal("\u001B[1m[$current/$total]\u001B[0m Downloading ${resource.name}...\r\n")
                    },
                    onResourceProgress = { resource, progress ->
                        val pct = (progress * 100).toInt()
                        writeToTerminal("\u001B[33m  ${resource.name}: $pct%\u001B[0m\r")
                    },
                    onResourceComplete = { resource ->
                        writeToTerminal("  \u001B[32m✓\u001B[0m Downloaded ${resource.name}\r\n")
                    },
                    onError = { error ->
                        writeToTerminal("\u001B[31mERROR: $error\u001B[0m\r\n")
                    }
                )
            }

            if (!success) {
                writeToTerminal("\r\n\u001B[31mDownload failed. Check your connection and try again.\u001B[0m\r\n")
                return@launch
            }

            writeToTerminal("\r\n\u001B[1mDownloads complete. Extracting...\u001B[0m\r\n\r\n")

            withContext(Dispatchers.IO) {
                for (resource in toInstall) {
                    writeToTerminal("Extracting ${resource.name}...\r\n")
                    val result = resourceManager.extractResource(resource, object : ResourceManager.ExtractionListener {
                        override fun onExtractionStart(fileName: String) {}
                        override fun onExtractionComplete(destDir: File) {
                            writeToTerminal("  \u001B[32m✓\u001B[0m Extracted to ${destDir.name}\r\n")
                        }
                        override fun onExtractionError(error: String) {
                            writeToTerminal("  \u001B[31m✗\u001B[0m $error\r\n")
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
        writeToTerminal("\u001B[1mFetching resource manifest...\u001B[0m\r\n")

        scope.launch {
            val manifestResult = withContext(Dispatchers.IO) { resourceManager.fetchManifest() }
            if (manifestResult.isFailure) {
                writeToTerminal("\u001B[31mERROR: Failed to fetch manifest: ${manifestResult.exceptionOrNull()?.message}\u001B[0m\r\n")
                writeToTerminal("\u001B[33mCheck your internet connection and try again.\u001B[0m\r\n")
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

            val success = withContext(Dispatchers.IO) {
                resourceManager.downloadResources(
                    requiredResources,
                    onResourceStart = { resource, current, total ->
                        writeToTerminal("\u001B[1m[$current/$total]\u001B[0m Downloading ${resource.name}...\r\n")
                    },
                    onResourceProgress = { resource, progress ->
                        val pct = (progress * 100).toInt()
                        writeToTerminal("\u001B[33m  ${resource.name}: $pct%\u001B[0m\r")
                    },
                    onResourceComplete = { resource ->
                        writeToTerminal("  \u001B[32m✓\u001B[0m Downloaded ${resource.name}\r\n")
                    },
                    onError = { error ->
                        writeToTerminal("\u001B[31mERROR: $error\u001B[0m\r\n")
                    }
                )
            }

            if (!success) {
                writeToTerminal("\r\n\u001B[31mDownload failed. Please check your connection and try again.\u001B[0m\r\n")
                return@launch
            }

            writeToTerminal("\r\n\u001B[1mAll downloads complete. Extracting...\u001B[0m\r\n")

            withContext(Dispatchers.IO) {
                for (resource in requiredResources) {
                    writeToTerminal("Extracting ${resource.name}...\r\n")
                    val result = resourceManager.extractResource(resource, object : ResourceManager.ExtractionListener {
                        override fun onExtractionStart(fileName: String) {}
                        override fun onExtractionComplete(destDir: File) {
                            writeToTerminal("  \u001B[32m✓\u001B[0m Extracted to ${destDir.name}\r\n")
                        }
                        override fun onExtractionError(error: String) {
                            writeToTerminal("  \u001B[31m✗\u001B[0m $error\r\n")
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
        if (setupCompleted) return
        setupCompleted = true
        SetupState.setSetupComplete(this, true)
        handler.postDelayed({
            TerminalService.stop(this)
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }, 1500)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        sessions.forEach { it.finishIfRunning() }
        if (isFinishing) {
            TerminalService.stop(this)
        }
    }

    override fun onTextChanged(changedSession: TerminalSession) {
        if (changedSession === currentSession) {
            handler.post { terminalView.onScreenUpdated() }
        }
    }

    override fun onTitleChanged(changedSession: TerminalSession) {
        handler.post { sessionAdapter.notifyDataSetChanged() }
    }

    override fun onSessionFinished(finishedSession: TerminalSession) {
        handler.post { sessionAdapter.notifyDataSetChanged() }
    }

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

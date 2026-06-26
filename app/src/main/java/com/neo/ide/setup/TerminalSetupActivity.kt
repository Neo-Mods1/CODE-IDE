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
import android.os.SystemClock
import android.util.TypedValue
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
import com.neo.ide.activities.HomeActivity
import com.neo.ide.download.SetupState
import android.content.Intent
import com.termux.shared.termux.extrakeys.ExtraKeysView
import com.termux.shared.termux.extrakeys.ExtraKeysConstants
import com.termux.shared.termux.extrakeys.ExtraKeysInfo
import com.termux.shared.termux.extrakeys.ExtraKeyButton
import com.termux.shared.termux.extrakeys.SpecialButton
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import com.termux.view.TerminalView
import com.termux.view.TerminalViewClient
import java.io.File

/**
 * Terminal activity that runs the idesetup.sh script during first-time setup.
 * Matches AndroidIDE's TerminalActivity pattern: copies script from assets,
 * creates a real terminal session, and executes the script with args.
 */
class TerminalSetupActivity : AppCompatActivity(), TerminalSessionClient {

    companion object {
        const val EXTRA_SETUP_ARGS = "setup_args"
        const val EXTRA_JDK_VERSION = "jdk_version"
        const val EXTRA_SDK_VERSION = "sdk_version"
        const val EXTRA_WITH_GIT = "with_git"
        const val EXTRA_WITH_OPENSSH = "with_openssh"
        const val EXTRA_MANIFEST_URL = "manifest_url"
    }

    private lateinit var terminalView: TerminalView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var extraKeysView: ExtraKeysView
    private lateinit var sessionListView: ListView
    private val handler = Handler(Looper.getMainLooper())

    private val sessions = mutableListOf<TerminalSession>()
    private var currentSession: TerminalSession? = null
    private var currentFontSize = 0
    private var sessionCounter = 0
    private var setupCompleted = false

    private lateinit var sessionAdapter: ArrayAdapter<String>
    private var idesetupSession: IdesetupSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminal_setup)

        window.decorView.setBackgroundColor(Color.BLACK)
        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK

        // If setup already complete, go straight to HomeActivity
        if (SetupState.isSetupComplete(this)) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        terminalView = findViewById(R.id.terminal_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        sessionListView = findViewById(R.id.terminal_sessions_list)
        extraKeysView = findViewById(R.id.extra_keys_view)
        extraKeysView.setExtraKeysViewClient(object : ExtraKeysView.IExtraKeysView {
            override fun onExtraKeyButtonClick(view: View, buttonInfo: ExtraKeyButton, button: com.google.android.material.button.MaterialButton) {
                val key = buttonInfo.key
                val session = currentSession ?: return
                val keyCode = ExtraKeysConstants.PRIMARY_KEY_CODES_FOR_STRINGS[key]
                if (keyCode != null) {
                    val event = KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, keyCode, 0)
                    terminalView.onKeyDown(keyCode, event)
                } else if (key.length == 1) {
                    session.write(key)
                }
            }
            override fun performExtraKeyButtonHapticFeedback(view: View, buttonInfo: ExtraKeyButton, button: com.google.android.material.button.MaterialButton) = false
        })

        // Load extra keys
        try {
            val configJson = "[[\"ESC\",\"/\",\"-\",\"HOME\",\"UP\",\"END\",\"PGUP\"],[\"TAB\",\"CTRL\",\"ALT\",\"LEFT\",\"DOWN\",\"RIGHT\",\"PGDN\"]]"
            val extraKeysInfo = ExtraKeysInfo(configJson, "default", ExtraKeysConstants.CONTROL_CHARS_ALIASES)
            extraKeysView.setButtonColors(
                0xFFFFFFFF.toInt(),
                0xFF80DEEA.toInt(),
                0x00000000,
                0xFF7F7F7F.toInt()
            )
            val heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 37.5f, resources.displayMetrics)
            extraKeysView.reload(extraKeysInfo, heightPx)
        } catch (e: Exception) {
            // Extra keys failed to load
        }

        // AndroidIDE default: 12sp font size
        currentFontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics).toInt()

        // Toggle keyboard button
        findViewById<TextView>(R.id.toggle_keyboard_button).setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.toggleSoftInput(0, 0)
        }

        // Session list adapter
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

        // Terminal view client
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
            override fun readControlKey(): Boolean = extraKeysView.readSpecialButton(SpecialButton.CTRL, false) ?: false
            override fun readAltKey(): Boolean = extraKeysView.readSpecialButton(SpecialButton.ALT, false) ?: false
            override fun readShiftKey(): Boolean = extraKeysView.readSpecialButton(SpecialButton.SHIFT, false) ?: false
            override fun readFnKey(): Boolean = extraKeysView.readSpecialButton(SpecialButton.FN, false) ?: false
            override fun onCodePoint(codePoint: Int, ctrlDown: Boolean, session: TerminalSession?): Boolean = false
            override fun onEmulatorSet() {}
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

        // Create session and run setup script
        ShellEnvironment.ensureDirectories(this)
        createSessionAndRunSetup()
    }

    private fun createSessionAndRunSetup() {
        // Create the setup script from assets
        val script = IdesetupSession.createScript(this)
        if (script == null) {
            Toast.makeText(this, "Failed to create setup script", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Build arguments from intent
        val args = buildSetupArguments()

        // Create terminal session with the script as executable
        val shell = getShellPath()
        val homeDir = File(filesDir, "home")
        if (!homeDir.exists()) homeDir.mkdirs()

        val environment = ShellEnvironment.buildEnvironment(this)

        val session = TerminalSession(
            script.absolutePath,  // executable = our setup script
            homeDir.absolutePath, // working directory
            environment,          // environment variables
            null,                 // stdin
            null,                 // session name
            this                  // client
        )
        session.mSessionName = "IDE setup"

        sessions.add(session)
        sessionAdapter.notifyDataSetChanged()

        // Store the session wrapper for cleanup
        idesetupSession = IdesetupSession(session, script)

        // Attach to terminal view
        switchToSession(session)
    }

    private fun buildSetupArguments(): Array<String> {
        val args = mutableListOf<String>()

        // Install directory
        args.add("--install-dir")
        args.add(filesDir.absolutePath + "/home")

        // SDK version
        val sdkVersion = intent.getStringExtra(EXTRA_SDK_VERSION) ?: "36"
        args.add("--sdk")
        args.add(sdkVersion)

        // JDK version
        val jdkVersion = intent.getStringExtra(EXTRA_JDK_VERSION) ?: "17"
        args.add("--jdk")
        args.add(jdkVersion)

        // Manifest URL
        val manifestUrl = intent.getStringExtra(EXTRA_MANIFEST_URL)
        if (manifestUrl != null) {
            args.add("--manifest")
            args.add(manifestUrl)
        }

        // Flags
        args.add("--assume-yes")

        if (intent.getBooleanExtra(EXTRA_WITH_GIT, false)) {
            args.add("--with-git")
        }
        if (intent.getBooleanExtra(EXTRA_WITH_OPENSSH, false)) {
            args.add("--with-openssh")
        }

        return args.toTypedArray()
    }

    private fun createNewSession(): TerminalSession {
        sessionCounter++
        val shell = getShellPath()
        val homeDir = File(filesDir, "home")
        if (!homeDir.exists()) homeDir.mkdirs()
        val cwd = homeDir.absolutePath
        val environment = ShellEnvironment.buildEnvironment(this)
        val session = TerminalSession(shell, cwd, environment, null, null, this)
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
        terminalView.attachSession(session)
        terminalView.setTextSize(currentFontSize)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (SetupState.isSetupComplete(this)) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        idesetupSession?.cleanup()
        sessions.forEach { it.finishIfRunning() }
        if (isFinishing) {
            TerminalService.stop(this)
        }
    }

    // TerminalSessionClient callbacks
    override fun onTextChanged(changedSession: TerminalSession) {
        if (changedSession === currentSession) {
            handler.post { terminalView.onScreenUpdated() }
        }
    }

    override fun onTitleChanged(changedSession: TerminalSession) {
        handler.post { sessionAdapter.notifyDataSetChanged() }
    }

    override fun onSessionFinished(finishedSession: TerminalSession) {
        handler.post {
            sessionAdapter.notifyDataSetChanged()
            // If the setup session finished successfully, mark setup complete
            if (finishedSession.exitStatus == 0) {
                finishSetup()
            }
        }
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

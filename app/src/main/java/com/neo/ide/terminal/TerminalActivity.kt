/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.terminal

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
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.neo.ide.R
import com.neo.ide.app.BaseActivity
import com.neo.ide.setup.ShellEnvironment
import com.neo.ide.setup.TerminalService
import com.termux.app.TermuxInstaller
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

class TerminalActivity : BaseActivity(), TerminalSessionClient {

    private lateinit var terminalView: TerminalView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var extraKeysView: ExtraKeysView
    private lateinit var sessionListView: ListView
    private val handler = Handler(Looper.getMainLooper())

    private val sessions = mutableListOf<TerminalSession>()
    private var currentSession: TerminalSession? = null
    private var currentFontSize = 0
    private var sessionCounter = 0

    private lateinit var sessionAdapter: ArrayAdapter<String>

    override fun bindLayout(): View {
        return layoutInflater.inflate(R.layout.activity_terminal_setup, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.setBackgroundColor(Color.BLACK)
        window.statusBarColor = Color.BLACK
        window.navigationBarColor = Color.BLACK

        // Make terminal resize when keyboard appears
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        terminalView = findViewById(R.id.terminal_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        sessionListView = findViewById(R.id.terminal_sessions_list)
        extraKeysView = findViewById(R.id.extra_keys_view)

        extraKeysView.setExtraKeysViewClient(object : ExtraKeysView.IExtraKeysView {
            override fun onExtraKeyButtonClick(view: View, buttonInfo: ExtraKeyButton, button: com.google.android.material.button.MaterialButton) {
                val session = currentSession ?: return
                val key = buttonInfo.key

                if (buttonInfo.isMacro) {
                    var ctrl = extraKeysView.readSpecialButton(SpecialButton.CTRL, false) ?: false
                    var alt = extraKeysView.readSpecialButton(SpecialButton.ALT, false) ?: false
                    var shift = extraKeysView.readSpecialButton(SpecialButton.SHIFT, false) ?: false
                    var fn = extraKeysView.readSpecialButton(SpecialButton.FN, false) ?: false
                    for (token in key.split(" ")) {
                        when (token) {
                            "CTRL" -> ctrl = true
                            "ALT" -> alt = true
                            "SHIFT" -> shift = true
                            "FN" -> fn = true
                            else -> {
                                sendKeyWithModifiers(token, ctrl, alt, shift, fn)
                                ctrl = extraKeysView.readSpecialButton(SpecialButton.CTRL, false) ?: false
                                alt = extraKeysView.readSpecialButton(SpecialButton.ALT, false) ?: false
                                shift = extraKeysView.readSpecialButton(SpecialButton.SHIFT, false) ?: false
                                fn = extraKeysView.readSpecialButton(SpecialButton.FN, false) ?: false
                            }
                        }
                    }
                } else {
                    val isCtrl = extraKeysView.readSpecialButton(SpecialButton.CTRL, false) ?: false
                    val isAlt = extraKeysView.readSpecialButton(SpecialButton.ALT, false) ?: false
                    val isShift = extraKeysView.readSpecialButton(SpecialButton.SHIFT, false) ?: false
                    val isFn = extraKeysView.readSpecialButton(SpecialButton.FN, false) ?: false
                    sendKeyWithModifiers(key, isCtrl, isAlt, isShift, isFn)
                }
            }
            override fun performExtraKeyButtonHapticFeedback(view: View, buttonInfo: ExtraKeyButton, button: com.google.android.material.button.MaterialButton) = false
        })

        try {
            val configJson = "[[\"ESC\",\"/\",\"-\",\"HOME\",\"UP\",\"END\",\"PGUP\",\"BKSP\"],[\"TAB\",\"CTRL\",\"ALT\",\"LEFT\",\"DOWN\",\"RIGHT\",\"PGDN\",\"DEL\"]]"
            val extraKeysInfo = ExtraKeysInfo(configJson, "default", ExtraKeysConstants.CONTROL_CHARS_ALIASES)
            extraKeysView.setButtonColors(
                0xFFFFFFFF.toInt(),
                0xFF80DEEA.toInt(),
                0x00000000,
                0xFF7F7F7F.toInt()
            )
            val heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 37.5f, resources.displayMetrics)
            extraKeysView.reload(extraKeysInfo, heightPx)
        } catch (_: Exception) {}

        currentFontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics).toInt()

        findViewById<TextView>(R.id.toggle_keyboard_button).setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.toggleSoftInput(0, 0)
        }

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

        findViewById<TextView>(R.id.new_session_button).setOnClickListener {
            createNewSession()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        terminalView.setTerminalViewClient(object : TerminalViewClient {
            override fun onScale(scale: Float): Float {
                if (scale < 0.9f || scale > 1.1f) {
                    changeFontSize(scale > 1f)
                    return 1.0f
                }
                return scale
            }
            override fun onSingleTapUp(e: MotionEvent) {
                terminalView.requestFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.showSoftInput(terminalView, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            }
            override fun shouldBackButtonBeMappedToEscape(): Boolean = false
            override fun shouldEnforceCharBasedInput(): Boolean = true
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

        TerminalService.start(this)
        ShellEnvironment.ensureDirectories(this)

        if (TermuxInstaller.isBootstrapInstalled(this)) {
            createNewSession()
        } else {
            TermuxInstaller.setupBootstrapIfNeeded(this, object : TermuxInstaller.SetupCallback {
                override fun onSuccess() {
                    ShellEnvironment.ensureDirectories(this@TerminalActivity)
                    createNewSession()
                }
                override fun onError(message: String) {
                    Toast.makeText(this@TerminalActivity, "Bootstrap failed: $message", Toast.LENGTH_LONG).show()
                    finish()
                }
            })
        }
    }

    private fun sendKeyWithModifiers(key: String, ctrl: Boolean, alt: Boolean, shift: Boolean, fn: Boolean) {
        val session = currentSession ?: return
        val keyCode = ExtraKeysConstants.PRIMARY_KEY_CODES_FOR_STRINGS[key]
        if (keyCode != null) {
            var metaState = 0
            if (ctrl) metaState = metaState or KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
            if (alt) metaState = metaState or KeyEvent.META_ALT_ON or KeyEvent.META_ALT_LEFT_ON
            if (shift) metaState = metaState or KeyEvent.META_SHIFT_ON or KeyEvent.META_SHIFT_LEFT_ON
            if (fn) metaState = metaState or KeyEvent.META_FUNCTION_ON
            val downEvent = KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_DOWN, keyCode, 0, metaState)
            val upEvent = KeyEvent(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, keyCode, 0, metaState)
            // Use direct onKeyDown/onKeyUp instead of dispatchKeyEvent to avoid focus issues
            terminalView.onKeyDown(keyCode, downEvent, session)
            terminalView.onKeyUp(keyCode, upEvent)
        } else if (key.length == 1) {
            val bytes = key.toByteArray(Charsets.UTF_8)
            session.write(bytes, 0, bytes.size)
        } else if (key == "KEYBOARD") {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.toggleSoftInput(0, 0)
        }
    }

    private fun createNewSession(): TerminalSession {
        sessionCounter++
        val shell = getShellPath()
        val homeDir = File(filesDir, "home")
        if (!homeDir.exists()) homeDir.mkdirs()
        val cwd = homeDir.absolutePath
        val environment = ShellEnvironment.buildEnvironment(this)
        val session = TerminalSession(shell, cwd, null, environment, null, this)
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

    private fun changeFontSize(increase: Boolean) {
        val delta = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 2f, resources.displayMetrics).toInt()
        currentFontSize = if (increase) currentFontSize + delta else currentFontSize - delta
        val minSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 4f, resources.displayMetrics).toInt()
        val maxSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 256f, resources.displayMetrics).toInt()
        currentFontSize = currentFontSize.coerceIn(minSize, maxSize)
        terminalView.setTextSize(currentFontSize)
    }

    private fun getShellPath(): String {
        val prefixBin = File(TermuxInstaller.getPrefixPath(this), "bin")
        val shells = listOf("login", "bash", "sh")
        for (shell in shells) {
            val f = File(prefixBin, shell)
            if (f.exists() && f.canExecute()) return f.absolutePath
        }
        return "/system/bin/sh"
    }

    override fun onResume() {
        super.onResume()
        terminalView.onResume()
        // Show keyboard when resuming if terminal has focus
        handler.postDelayed({
            if (currentSession != null) {
                terminalView.requestFocus()
                val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.showSoftInput(terminalView, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
            }
        }, 200)
    }

    override fun onPause() {
        super.onPause()
        terminalView.onPause()
        // Hide keyboard when pausing
        val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(terminalView.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
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

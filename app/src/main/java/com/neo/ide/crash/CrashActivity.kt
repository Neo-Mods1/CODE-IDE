/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.crash

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.neo.ide.R

class CrashActivity : AppCompatActivity() {

    private lateinit var logTextView: TextView
    private lateinit var scrollView: ScrollView
    private var fullLog: String = ""

    companion object {
        const val EXTRA_CRASH_LOG = "crash_log"
        const val EXTRA_CRASH_MESSAGE = "crash_message"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)

        val messageText = findViewById<TextView>(R.id.crash_message)
        val loadingText = findViewById<TextView>(R.id.crash_loading)
        scrollView = findViewById(R.id.crash_scroll_view)
        logTextView = findViewById(R.id.crash_log_text)
        val copyButton = findViewById<Button>(R.id.crash_copy_btn)
        val restartButton = findViewById<Button>(R.id.crash_restart_btn)

        messageText.text = intent.getStringExtra(EXTRA_CRASH_MESSAGE) ?: "Unknown error occurred"

        copyButton.setOnClickListener { copyLogToClipboard() }
        restartButton.setOnClickListener { restartApp() }

        fullLog = intent.getStringExtra(EXTRA_CRASH_LOG) ?: "No crash log available"

        loadingText.visibility = android.view.View.GONE
        logTextView.text = fullLog
    }

    private fun copyLogToClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Crash Log", fullLog)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Crash log copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun restartApp() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        finish()
        Process.killProcess(Process.myPid())
    }
}

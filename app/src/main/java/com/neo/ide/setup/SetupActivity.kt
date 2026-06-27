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

package com.neo.ide.setup

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.neo.ide.R
import com.neo.ide.activities.HomeActivity
import com.neo.ide.app.BaseActivity
import com.neo.ide.download.SetupState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SetupActivity : BaseActivity() {

    companion object {
        const val EXTRA_PLATFORM_TAG = "platform_tag"
        const val EXTRA_JDK_TAG = "jdk_tag"
        const val EXTRA_NDK_TAG = "ndk_tag"
    }

    private lateinit var logText: TextView
    private lateinit var logScroll: ScrollView
    private lateinit var statusText: TextView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var progressText: TextView

    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val logBuilder = SpannableStringBuilder()

    override fun bindLayout(): View {
        return layoutInflater.inflate(R.layout.activity_setup, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SetupState.isSetupComplete(this)) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        logText = findViewById(R.id.log_text)
        logScroll = findViewById(R.id.log_scroll)
        statusText = findViewById(R.id.status_text)
        progressBar = findViewById(R.id.progress_bar)
        progressText = findViewById(R.id.progress_text)

        progressBar.isIndeterminate = true

        appendLog("CODE-IDE Setup", LogColor.HEADER)
        appendLog("─────────────────────────────────", LogColor.DIM)
        appendLog("", LogColor.DIM)
        startSetup()
    }

    private fun startSetup() {
        val installer = SetupInstaller(this)

        val platformTag = intent.getStringExtra(EXTRA_PLATFORM_TAG) ?: ""
        val jdkTag = intent.getStringExtra(EXTRA_JDK_TAG) ?: ""
        val ndkTag = intent.getStringExtra(EXTRA_NDK_TAG) ?: ""

        scope.launch {
            installer.runSetup(
                platformCategory = platformTag,
                jdkCategory = jdkTag,
                ndkCategory = ndkTag,
                listener = object : SetupInstaller.SetupListener {
                    override fun onProgress(progress: SetupInstaller.SetupProgress) {
                        handler.post {
                            when {
                                progress.isComplete -> {
                                    appendLog("", LogColor.DIM)
                                    appendLog("✓ ${progress.message}", LogColor.SUCCESS)
                                    statusText.text = "Setup complete"
                                    progressBar.isIndeterminate = false
                                    progressBar.progress = 100
                                    progressText.text = ""
                                    finishSetup()
                                }
                                progress.isError -> {
                                    appendLog("✗ ${progress.message}", LogColor.ERROR)
                                    statusText.text = "Setup failed"
                                    progressBar.isIndeterminate = false
                                    progressBar.progress = 0
                                    progressText.text = progress.message
                                }
                                progress.progress >= 0f -> {
                                    statusText.text = progress.message
                                    progressBar.isIndeterminate = false
                                    progressBar.progress = (progress.progress * 100).toInt()
                                    progressText.text = "${(progress.progress * 100).toInt()}%"
                                }
                                else -> {
                                    statusText.text = progress.message
                                    appendLog("→ ${progress.message}", LogColor.INFO)
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    private fun appendLog(text: String, color: LogColor) {
        if (text.isEmpty()) {
            logBuilder.append("\n")
            updateLog()
            return
        }
        val start = logBuilder.length
        logBuilder.append(text)
        logBuilder.append("\n")
        logBuilder.setSpan(
            ForegroundColorSpan(color.color),
            start,
            start + text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        updateLog()
    }

    private fun updateLog() {
        logText.text = logBuilder
        handler.postDelayed({ logScroll.fullScroll(View.FOCUS_DOWN) }, 50)
    }

    private fun finishSetup() {
        SetupState.setSetupComplete(this, true)
        handler.postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 1500)
    }

    private enum class LogColor(val color: Int) {
        HEADER(0xFF1565C0.toInt()),
        INFO(0xFF424242.toInt()),
        SUCCESS(0xFF2E7D32.toInt()),
        ERROR(0xFFC62828.toInt()),
        DIM(0xFF9E9E9E.toInt())
    }
}

/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.crash

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import com.neo.ide.BuildConfig
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
    private var crashLog = StringBuilder()

    fun init() {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            crashLog.clear()
            crashLog.appendLine("========================================")
            crashLog.appendLine("  CODE-IDE - Crash Report")
            crashLog.appendLine("========================================")
            crashLog.appendLine()
            crashLog.appendLine("Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())}")
            crashLog.appendLine("Thread: ${thread.name}")
            crashLog.appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            crashLog.appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            crashLog.appendLine("App Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            crashLog.appendLine()
            crashLog.appendLine("========================================")
            crashLog.appendLine("  Stack Trace")
            crashLog.appendLine("========================================")
            crashLog.appendLine()

            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))
            crashLog.appendLine(sw.toString())

            crashLog.appendLine()
            crashLog.appendLine("========================================")
            crashLog.appendLine("  Logcat (last 200 lines)")
            crashLog.appendLine("========================================")
            crashLog.appendLine()

            try {
                val process = Runtime.getRuntime().exec(arrayOf("logcat", "-d", "-t", "200", "*:E"))
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    crashLog.appendLine(line)
                }
                reader.close()
            } catch (e: Exception) {
                crashLog.appendLine("Failed to read logcat: ${e.message}")
            }

            val intent = Intent(context, CrashActivity::class.java).apply {
                putExtra(CrashActivity.EXTRA_CRASH_LOG, crashLog.toString())
                putExtra(CrashActivity.EXTRA_CRASH_MESSAGE, throwable.message ?: "Unknown error")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            context.startActivity(intent)

            Process.killProcess(Process.myPid())
            System.exit(1)

        } catch (e: Exception) {
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    fun getCrashLog(): String = crashLog.toString()
}

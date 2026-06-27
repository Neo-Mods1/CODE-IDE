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

package com.neo.ide.app

import android.app.Application
import android.util.Log
import com.neo.ide.BuildConfig
import com.neo.ide.download.SetupState
import java.io.File

/**
 * Base application class.
 * Initializes environment, paths, and global state on startup.
 * Adapted from AndroidIDE's BaseApplication.
 */
open class BaseApplication : Application() {

    companion object {
        lateinit var instance: BaseApplication
            private set

        val PROJECTS_DIR: File
            get() = File(instance.filesDir, "home/projects")

        val HOME_DIR: File
            get() = File(instance.filesDir, "home")

        val PREFIX_DIR: File
            get() = File(instance.filesDir, "usr")

        val CACHE_DIR: File
            get() = File(instance.filesDir, "home/.cache")

        val ANDROID_HOME: File
            get() = File(HOME_DIR, "android-sdk")

        val JAVA_HOME: File
            get() = File(PREFIX_DIR, "opt/openjdk")

        val TERMINAL_HOME: File
            get() = HOME_DIR
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        initDirectories()
        Log.i("BaseApplication", "CODE-IDE v${BuildConfig.VERSION_NAME} started")
    }

    private fun initDirectories() {
        val dirs = listOf(
            HOME_DIR,
            File(HOME_DIR, "tmp"),
            CACHE_DIR,
            PROJECTS_DIR,
            PREFIX_DIR,
            File(PREFIX_DIR, "bin"),
            File(PREFIX_DIR, "opt"),
            File(PREFIX_DIR, "etc"),
            ANDROID_HOME,
            JAVA_HOME
        )
        dirs.forEach { it.mkdirs() }
    }

    fun getPrefManager(): android.content.SharedPreferences {
        return getSharedPreferences("code_ide_prefs", MODE_PRIVATE)
    }

    fun openUrl(url: String) {
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("BaseApplication", "Failed to open URL: $url", e)
        }
    }

    fun openTelegram(url: String) {
        try {
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
            intent.setPackage("org.telegram.messenger")
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            openUrl(url)
        }
    }

    fun openTelegramGroup() = openTelegram("https://t.me/NeoModsChannel")
    fun openTelegramChannel() = openTelegram("https://t.me/NeoModsChannel")
    fun openGitHub() = openUrl("https://github.com/Neo-Mods1/CODE-IDE")
}

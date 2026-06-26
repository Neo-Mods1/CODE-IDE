/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.setup

import android.content.Context
import java.io.File

/**
 * Sets up the shell environment for terminal sessions.
 * Matches AndroidIDE's environment variable layout.
 */
object ShellEnvironment {

    /**
     * Build environment array for a terminal session.
     * Returns array of "KEY=VALUE" strings.
     */
    fun buildEnvironment(context: Context, extraEnv: Map<String, String> = emptyMap()): Array<String> {
        val homeDir = File(context.filesDir, "home")
        val prefixDir = File(context.filesDir, "usr")
        val androidHome = File(homeDir, "android-sdk")

        val env = mutableMapOf<String, String>()

        // Base paths
        env["HOME"] = homeDir.absolutePath
        env["PREFIX"] = prefixDir.absolutePath
        env["TMPDIR"] = File(homeDir, "tmp").absolutePath

        // Android SDK
        env["ANDROID_HOME"] = androidHome.absolutePath
        env["ANDROID_SDK_ROOT"] = androidHome.absolutePath

        // JDK — default to 17, can be overridden
        val jdk17 = File(prefixDir, "opt/openjdk-17")
        val jdk21 = File(prefixDir, "opt/openjdk-21")
        val javaHome = when {
            jdk21.exists() -> jdk21.absolutePath
            jdk17.exists() -> jdk17.absolutePath
            else -> File(prefixDir, "opt/openjdk").absolutePath
        }
        env["JAVA_HOME"] = javaHome

        // PATH
        val pathParts = mutableListOf<String>()
        pathParts.add(File(prefixDir, "bin").absolutePath)
        pathParts.add(File(androidHome, "cmdline-tools/latest/bin").absolutePath)
        pathParts.add(File(androidHome, "platform-tools").absolutePath)
        env["PATH"] = pathParts.joinToString(":")

        // LANG
        env["LANG"] = "en_US.UTF-8"
        env["LC_ALL"] = "en_US.UTF-8"

        // Termux compatibility
        env["SYSROOT"] = prefixDir.absolutePath

        // Apply extra environment
        env.putAll(extraEnv)

        return env.map { "${it.key}=${it.value}" }.toTypedArray()
    }

    /**
     * Ensure required directories exist.
     */
    fun ensureDirectories(context: Context) {
        val homeDir = File(context.filesDir, "home")
        val prefixDir = File(context.filesDir, "usr")

        listOf(
            homeDir,
            File(homeDir, "tmp"),
            File(homeDir, ".cache"),
            File(homeDir, "android-sdk"),
            File(prefixDir, "bin"),
            File(prefixDir, "opt"),
            File(prefixDir, "etc")
        ).forEach { it.mkdirs() }
    }
}

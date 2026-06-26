/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.setup

import android.content.Context
import com.termux.terminal.TerminalSession
import java.io.File
import java.io.FileOutputStream

/**
 * Wraps a terminal session that runs the idesetup.sh script.
 * Copies the script from assets to a temp file, sets execute permissions,
 * and cleans up when the session finishes.
 */
class IdesetupSession(
    val terminalSession: TerminalSession,
    private val scriptFile: File
) {

    companion object {

        private const val SCRIPT_ASSET_PATH = "data/common/idesetup.sh"

        /**
         * Create the setup script from assets.
         * Returns a temporary executable file, or null on failure.
         */
        fun createScript(context: Context): File? {
            return try {
                val script = File(context.cacheDir, "idesetup.sh")

                context.assets.open(SCRIPT_ASSET_PATH).use { input ->
                    FileOutputStream(script).use { output ->
                        input.copyTo(output)
                    }
                }

                // Set permissions: owner r-x
                script.setReadable(true, false)
                script.setExecutable(true, false)
                script.setWritable(false, false)

                script
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Delete the temporary script file.
     */
    fun cleanup() {
        try {
            if (scriptFile.exists()) {
                scriptFile.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

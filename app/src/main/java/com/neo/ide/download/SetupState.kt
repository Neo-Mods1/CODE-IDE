/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.download

import android.content.Context
import android.os.Environment
import android.provider.Settings
import android.net.Uri
import android.os.Build

object SetupState {
    private const val PREFS_NAME = "setup_state"
    private const val KEY_SETUP_COMPLETE = "setup_complete"
    private const val KEY_PERMISSIONS_GRANTED = "permissions_granted"
    private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isSetupComplete(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_SETUP_COMPLETE, false)
    }

    fun setSetupComplete(context: Context, complete: Boolean) {
        prefs(context).edit().putBoolean(KEY_SETUP_COMPLETE, complete).apply()
    }

    fun isOnboardingComplete(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_ONBOARDING_COMPLETE, false)
    }

    fun setOnboardingComplete(context: Context, complete: Boolean) {
        prefs(context).edit().putBoolean(KEY_ONBOARDING_COMPLETE, complete).apply()
    }

    fun arePermissionsGranted(context: Context): Boolean {
        val storageGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        val installGranted = context.packageManager.canRequestPackageInstalls()
        return storageGranted && installGranted
    }

    private const val KEY_STATISTICS_SKIPPED = "statistics_skipped"

    fun isStatisticsSkipped(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_STATISTICS_SKIPPED, false)
    }

    fun setStatisticsSkipped(context: Context, skipped: Boolean) {
        prefs(context).edit().putBoolean(KEY_STATISTICS_SKIPPED, skipped).apply()
    }
}

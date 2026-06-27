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

package com.neo.ide.utils

import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes

fun flashError(msg: String?) {
    withActivity { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
}

fun flashError(@StringRes msg: Int) {
    withActivity { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
}

fun flashSuccess(msg: String?) {
    withActivity { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
}

fun flashSuccess(@StringRes msg: Int) {
    withActivity { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
}

fun flashInfo(msg: String?) {
    withActivity { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
}

fun flashInfo(@StringRes msg: Int) {
    withActivity { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
}

private fun <T> withActivity(action: Activity.() -> T?): T? {
    val activity = com.neo.ide.utils.ActivityUtils.getTopActivity()
    return activity?.let { it.action() }
}

object ActivityUtils {
    fun getTopActivity(): Activity? = null
}

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

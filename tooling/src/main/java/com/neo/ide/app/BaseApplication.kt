package com.neo.ide.app

import android.app.Application

open class BaseApplication : Application() {
    companion object {
        @JvmStatic
        lateinit var instance: BaseApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    open fun getStringByName(name: String): String? {
        val resId = resources.getIdentifier(name, "string", packageName)
        return if (resId != 0) getString(resId) else null
    }
}

/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide

import android.app.Application
import com.neo.ide.crash.CrashHandler

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        CrashHandler(this).init()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}

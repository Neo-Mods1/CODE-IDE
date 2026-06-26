/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide

import com.neo.ide.app.BaseApplication
import com.neo.ide.crash.CrashHandler

class App : BaseApplication() {

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

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

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Base activity class.
 * Provides coroutine scope, system bar theming, and common activity utilities.
 * Adapted from AndroidIDE's BaseIDEActivity.
 */
abstract class BaseActivity : AppCompatActivity() {

    val activityScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    open val enableSystemBarTheming: Boolean = true
    open val subscribeToEvents: Boolean = false

    open val navigationBarColor: Int
        get() = android.graphics.Color.BLACK

    open val statusBarColor: Int
        get() = android.graphics.Color.BLACK

    override fun onCreate(savedInstanceState: Bundle?) {
        if (enableSystemBarTheming) {
            applySystemBarColors()
        }

        super.onCreate(savedInstanceState)
        setContentView(bindLayout())
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }

    /**
     * Subclasses must implement this to return their root view.
     */
    protected abstract fun bindLayout(): View

    /**
     * Override to perform setup before setContentView.
     */
    protected open fun preSetContentLayout() {}

    /**
     * Apply system bar colors from the activity's properties.
     */
    protected open fun applySystemBarColors() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightNavigationBars = false
            isAppearanceLightStatusBars = false
        }
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
    }

    /**
     * Helper to replace a fragment in a container.
     */
    protected fun loadFragment(fragment: androidx.fragment.app.Fragment, containerId: Int) {
        supportFragmentManager.beginTransaction()
            .replace(containerId, fragment)
            .commit()
    }

    /**
     * Resolve a theme attribute to its integer value.
     */
    protected fun resolveAttr(attrRes: Int): Int {
        val typedValue = android.util.TypedValue()
        return if (theme.resolveAttribute(attrRes, typedValue, true)) {
            typedValue.data
        } else {
            0
        }
    }
}

/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
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

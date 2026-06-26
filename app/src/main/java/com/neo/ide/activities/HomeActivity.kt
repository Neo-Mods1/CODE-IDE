/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.activities

import android.os.Bundle
import android.view.View
import com.google.android.material.appbar.MaterialToolbar
import com.neo.ide.R
import com.neo.ide.app.BaseActivity

class HomeActivity : BaseActivity() {

    override val enableSystemBarTheming = true

    override fun bindLayout(): View {
        return layoutInflater.inflate(R.layout.activity_home, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, com.neo.ide.fragments.HomeFragment())
                .commit()
        }
    }
}

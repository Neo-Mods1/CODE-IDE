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



package com.neo.ide.actions.sidebar

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.neo.ide.R
import com.neo.ide.actions.ActionData
import com.neo.ide.actions.requireContext
import com.neo.ide.activities.PreferencesActivity
import kotlin.reflect.KClass

/**
 * A sidebar action to navigate the user to IDE Preferences.
 *
 * @author Akash Yadav
 */
class PreferencesSidebarAction(context: Context, override val order: Int) : AbstractSidebarAction() {

  override val id: String = "ide.editor.sidebar.preferences"

  // TODO : Should we show the preferences in the sidebar itself?
  override val fragmentClass: KClass<out Fragment>? = null

  init {
    label = context.getString(R.string.ide_preferences)
    icon = ContextCompat.getDrawable(context, R.drawable.ic_settings)
  }

  override suspend fun execAction(data: ActionData): Any {
    val context = data.requireContext()
    return context.startActivity(Intent(context, PreferencesActivity::class.java))
  }
}
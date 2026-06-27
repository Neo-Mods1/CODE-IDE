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



package com.neo.ide.preferences

import android.content.Context
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.PorterDuffColorFilter
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import com.neo.ide.utils.resolveAttr

/**
 * Base class for preferences.
 *
 * @author Akash Yadav
 */
abstract class BasePreference : IPreference() {

  abstract fun onCreatePreference(context: Context): Preference

  override fun onCreateView(context: Context): Preference {
    val pref = onCreatePreference(context)
    pref.key = this.key
    pref.title = context.getString(this.title)
    this.summary?.let { pref.summary = context.getString(it) }

    pref.isIconSpaceReserved = this.icon != null
    this.icon?.let {
      pref.icon =
        ContextCompat.getDrawable(context, it)?.apply {
          colorFilter =
            PorterDuffColorFilter(context.resolveAttr(R.attr.colorOnPrimaryContainer), SRC_ATOP)
        }
    }

    pref.setOnPreferenceClickListener { onPreferenceClick(pref) }
    pref.setOnPreferenceChangeListener(this::onPreferenceChanged)
    return pref
  }

  protected open fun onPreferenceChanged(preference: Preference, newValue: Any?): Boolean {
    return false
  }

  protected open fun onPreferenceClick(preference: Preference): Boolean {
    return false
  }
}

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
import androidx.preference.Preference
import androidx.preference.PreferenceCategory

/**
 * A group of preferences.
 *
 * @author Akash Yadav
 */
abstract class IPreferenceGroup : BasePreference() {

  /** The preferences. */
  abstract val children: List<IPreference>

  /** Adds the given preference to the preferences list. */
  fun addPreference(preference: IPreference) {
    (children as MutableList).add(preference)
  }

  /** Removes the given preference. */
  fun removePreference(preference: IPreference) {
    (children as MutableList).remove(preference)
  }

  /** Removes the preference at the given index. */
  fun removePreference(index: Int) {
    (children as MutableList).removeAt(index)
  }

  override fun onCreatePreference(context: Context): Preference {
    return PreferenceCategory(context)
  }
}

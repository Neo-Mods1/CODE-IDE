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
import kotlin.reflect.KMutableProperty0

/**
 * A switch preference.
 *
 * @author Akash Yadav
 */
abstract class SwitchPreference
@JvmOverloads
constructor(val setValue: ((Boolean) -> Unit)? = null, val getValue: (() -> Boolean)? = null) :
  BasePreference() {

    constructor(property: KMutableProperty0<Boolean>) : this(property::set, property::get)

  override fun onCreatePreference(context: Context): Preference {
    val pref = androidx.preference.SwitchPreference(context)
    pref.isChecked = prefValue()
    return pref
  }
  
  override fun onPreferenceChanged(preference: Preference, newValue: Any?): Boolean {
    setValue?.let { it(newValue as Boolean? ?: prefValue()) }
    return true
  }
  
  private fun prefValue(): Boolean {
    return getValue?.let { it() } ?: false
  }
}

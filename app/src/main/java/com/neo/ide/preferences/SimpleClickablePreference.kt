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

import androidx.preference.Preference
import kotlinx.parcelize.Parcelize

/**
 * A simple preference which is expected to be clickable only.
 *
 * @author Akash Yadav
 */
@Parcelize
class SimpleClickablePreference
@JvmOverloads
constructor(
  override val key: String,
  override val title: Int,
  override val summary: Int? = null,
  override val icon: Int? = null,
  private val onClick: ((Preference) -> Boolean)? = { false }
) : SimplePreference() {

  override fun onPreferenceClick(preference: Preference): Boolean {
    return onClick?.let { it(preference) } ?: false
  }
}

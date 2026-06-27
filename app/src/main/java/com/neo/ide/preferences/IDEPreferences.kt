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

import android.os.Parcel
import android.os.Parcelable

/**
 * The preferences for the IDE.
 *
 * @author Akash Yadav
 */
data object IDEPreferences : BaseIDEPreferences() {

  override val children: List<IPreference> = mutableListOf()
  override fun describeContents(): Int = 0
  override fun writeToParcel(dest: Parcel, flags: Int) {}
  
  @JvmField
  val CREATOR = object : Parcelable.Creator<IDEPreferences> {
    override fun createFromParcel(source: Parcel?): IDEPreferences {
      return IDEPreferences
    }
  
    override fun newArray(size: Int): Array<IDEPreferences> {
      return Array(size) { IDEPreferences }
    }
  }
}
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

import androidx.annotation.RestrictTo
import androidx.preference.Preference

/**
 * A preference with choices.
 *
 * @author Akash Yadav
 */
interface PreferenceChoices {

  /**
   * Get the entries for this preference.
   */
  fun getEntries(preference: Preference): Array<Entry>

  /**
   * Called when an item is selected from the single choice list.
   *
   * @param position The position of the selected item.
   * @param isSelected Whether the item is selected.
   */
  fun onSelectionChanged(preference: Preference, entry: Entry, position: Int, isSelected: Boolean)

  /**
   * Called when the user confirms the selections.
   *
   * @param entries The entries.
   */
  fun onChoicesConfirmed(preference: Preference, entries: Array<Entry>)

  /**
   * Called when the user cancels the selections.
   */
  fun onChoicesCancelled(preference: Preference)

  /**
   * Entry in [PreferenceChoices].
   *
   * @property label The label for the entry.
   * @property isChecked Whether the item is checked or not.
   * @property data The data object for the value.
   */
  data class Entry(
    val label: CharSequence,
    @Suppress("PropertyName") internal var _isChecked: Boolean,
    val data: Any,
  ) {

    val isChecked: Boolean
      get() = _isChecked

    companion object {

      @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
      val EMPTY = Entry("", false, 0)
    }
  }
}

/**
 * Map this [PreferenceChoices.Entry] array to entry labels.
 */
internal val Array<PreferenceChoices.Entry>.labels: Array<CharSequence>
  get() = Array(size) { this[it].label }
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * A preference which allows selecting a single value from a list of values.
 *
 * The [onSelectionChanged] method is called exactly two times when the user changes the selection, first call for the previously
 * selected item and second call for the newly selected item.
 *
 * The [onChoicesConfirmed] is always called with a singleton list.
 *
 * @author Akash Yadav
 */
abstract class SingleChoicePreference : ChoiceBasedDialogPreference(), PreferenceChoices {

  /**
   * The currently selected item in the dialog.
   */
  protected open var currentSelection: Int = -1

  override fun onConfigureDialogChoices(
    preference: Preference,
    dialog: MaterialAlertDialogBuilder,
    entries: Array<PreferenceChoices.Entry>,
    selections: BooleanArray
  ) {
    currentSelection = entries.indexOfFirst { it.isChecked }

    dialog.setSingleChoiceItems(
      entries.labels,
      currentSelection
    )
    { _, position ->
      if (currentSelection != -1) {
        onSelectionChanged(preference, entries[currentSelection], currentSelection, false)
      }

      currentSelection = position
      onSelectionChanged(preference, entries[currentSelection], position, true)
    }
  }

  override fun onChoicesConfirmed(
    preference: Preference,
    entries: Array<PreferenceChoices.Entry>
  ) {
    if (currentSelection < 0 || currentSelection > entries.lastIndex) {
      onChoiceConfirmed(preference, null, currentSelection)
    } else {
      onChoiceConfirmed(preference, entries[currentSelection], currentSelection)
    }
  }

  protected open fun onChoiceConfirmed(
    preference: Preference,
    entry: PreferenceChoices.Entry?,
    position: Int
  ) {
  }
}

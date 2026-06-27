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

import androidx.annotation.CallSuper
import androidx.preference.Preference
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Base class for dialog preferences which allows users to choose from multiple items. Subclasses
 * must call [onSelectionChanged] to notify about selection changes.
 *
 * @author Akash Yadav
 */
abstract class ChoiceBasedDialogPreference : DialogPreference(), PreferenceChoices {

  private var choices = emptyArray<PreferenceChoices.Entry>()

  final override fun onConfigureDialog(preference: Preference, dialog: MaterialAlertDialogBuilder) {
    choices = getEntries(preference)

    val selections = BooleanArray(choices.size) { choices[it].isChecked }
    onConfigureDialogChoices(preference, dialog, choices, selections)

    dialog.setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
      dialogInterface.dismiss()
      onChoicesConfirmed(preference, choices)
    }

    dialog.setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
      dialogInterface.dismiss()
      onChoicesCancelled(preference)
    }
  }

  @CallSuper
  override fun onSelectionChanged(
    preference: Preference,
    entry: PreferenceChoices.Entry,
    position: Int,
    isSelected: Boolean
  ) {
    entry._isChecked = isSelected
  }

  /**
   * Configure the dialog choices.
   */
  protected abstract fun onConfigureDialogChoices(
    preference: Preference,
    dialog: MaterialAlertDialogBuilder,
    entries: Array<PreferenceChoices.Entry>,
    selections: BooleanArray
  )

  override fun onChoicesConfirmed(preference: Preference, entries: Array<PreferenceChoices.Entry>) {
  }

  override fun onChoicesCancelled(preference: Preference) {}
}
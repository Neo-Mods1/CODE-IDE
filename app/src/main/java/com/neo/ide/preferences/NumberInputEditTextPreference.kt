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

import android.text.method.DigitsKeyListener
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.preference.Preference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.neo.ide.preferences.databinding.LayoutDialogTextInputBinding

/**
 * A preference which shows an edittext
 *
 * @author Akash Yadav
 */
abstract class NumberInputEditTextPreference(
  val hint: Int? = null,
  val setValue: ((Int) -> Unit)? = null,
  val getValue: (() -> Int)? = null
) : DialogPreference() {

  override fun onConfigureDialog(preference: Preference, dialog: MaterialAlertDialogBuilder) {
    super.onConfigureDialog(preference, dialog)
    val binding = LayoutDialogTextInputBinding.inflate(LayoutInflater.from(dialog.context))
    onConfigureTextInput(binding.name)
    dialog.setView(binding.root)
    dialog.setPositiveButton(android.R.string.ok) { iface, _ ->
      iface.dismiss()
      onPreferenceChanged(preference, binding.name.editText?.text?.toString()?.trim())
    }
    dialog.setNegativeButton(android.R.string.cancel) { iface, _ -> iface.dismiss() }
  }

  protected open fun onConfigureTextInput(input: TextInputLayout) {
    input.startIconDrawable = null
    hint?.let { input.setHint(it) } ?: run { input.hint = "" }
    input.editText?.apply {
      inputType = EditorInfo.TYPE_NUMBER_FLAG_SIGNED
      imeOptions = EditorInfo.IME_ACTION_DONE
      keyListener = DigitsKeyListener.getInstance("0123456789")
      setText(prefValue())
    }
  }

  override fun onPreferenceChanged(preference: Preference, newValue: Any?): Boolean {
    setValue?.let { it((newValue as String?)?.toInt() ?: 0) }
    return true
  }

  private fun prefValue(): String {
    return getValue?.let { it() }?.toString() ?: ""
  }
}

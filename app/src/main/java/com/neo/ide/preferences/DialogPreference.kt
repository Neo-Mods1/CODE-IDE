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
import com.neo.ide.utils.DialogUtils

/**
 * A preference which shows a dialog when clicked.
 *
 * @author Akash Yadav
 */
abstract class DialogPreference : SimplePreference() {

  open val dialogTitle: Int
    get() = this.title

  open val dialogMessage: Int? = null
  open val dialogCancellable: Boolean = false

  override fun onPreferenceClick(preference: Preference): Boolean {
    val dialog = DialogUtils.newMaterialDialogBuilder(preference.context)
    dialog.setTitle(this.dialogTitle)
    dialogMessage?.let { dialog.setMessage(it) }
    dialog.setCancelable(this.dialogCancellable)
    onConfigureDialog(preference, dialog)
    dialog.show()
    return true
  }

  protected open fun onConfigureDialog(preference: Preference,
    dialog: MaterialAlertDialogBuilder) {
  }
}

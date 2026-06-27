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


package com.neo.ide.fragments.output

import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ThreadUtils
import com.neo.ide.R

class BuildOutputFragment : NonEditableEditorFragment() {
  private val unsavedLines: MutableList<String?> = ArrayList()
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    emptyStateViewModel.emptyMessage.value = getString(R.string.msg_emptyview_buildoutput)
    if (unsavedLines.isNotEmpty()) {
      for (line in unsavedLines) {
        editor?.append("${line!!.trim()}\n")
      }
      unsavedLines.clear()
    }
  }
  
  override fun onDestroyView() {
    editor?.release()
    super.onDestroyView()
  }
  
  fun appendOutput(output: String?) {
    if (editor == null) {
      unsavedLines.add(output)
      return
    }
    ThreadUtils.runOnUiThread {
      val message = if (output == null || output.endsWith("\n")) {
        output
      } else {
        "${output}\n"
      }
      editor!!.append(message).also {
        emptyStateViewModel.isEmpty.value = false
      }
    }
  }
}
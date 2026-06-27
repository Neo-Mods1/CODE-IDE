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



package com.neo.ide.editor.ui

import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.updateLayoutParams
import com.neo.ide.editor.R
import io.github.rosemoe.sora.widget.component.DefaultCompletionLayout
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

/**
 * An implementation of [DefaultCompletionLayout] which customizes some attributes of the default
 * completion window layout.
 *
 * @author Akash Yadav
 */
class EditorCompletionLayout : DefaultCompletionLayout() {

  override fun onApplyColorScheme(colorScheme: EditorColorScheme) {

    val resources = completionList.context.resources
    val cornerRadius = resources.getDimensionPixelSize(R.dimen.completion_window_corner_radius)
      .toFloat()

    val strokeWidth = resources
      .getDimensionPixelSize(R.dimen.completion_window_stroke_width)

    (completionList.parent as? ViewGroup?)?.background = GradientDrawable().apply {
      setCornerRadius(cornerRadius)
      setStroke(strokeWidth, colorScheme.getColor(EditorColorScheme.COMPLETION_WND_CORNER))
      setColor(colorScheme.getColor(EditorColorScheme.COMPLETION_WND_BACKGROUND))
    }

    if (completionList.layoutParams is MarginLayoutParams) {
      completionList.updateLayoutParams<MarginLayoutParams> {
        marginStart = strokeWidth
        topMargin = strokeWidth
        marginEnd = strokeWidth
        bottomMargin = strokeWidth
      }
    }
  }
}

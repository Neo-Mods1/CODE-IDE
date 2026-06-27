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

package com.neo.ide.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout

// https://stackoverflow.com/a/65605542
class ScalableTabLayout : TabLayout {
  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr)

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val tabLayout = getChildAt(0) as ViewGroup
    val childCount = tabLayout.childCount
    if (childCount > 0) {
      val widthPixels = MeasureSpec.getSize(widthMeasureSpec)
      val tabMinWidth = widthPixels / childCount
      var remainderPixels = widthPixels % childCount
      for (i in 0 until childCount) {
        val it = tabLayout.getChildAt(i)
        if (remainderPixels > 0) {
          it.minimumWidth = tabMinWidth + 1
          remainderPixels--
        } else {
          it.minimumWidth = tabMinWidth
        }
      }
    }
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
  }
}

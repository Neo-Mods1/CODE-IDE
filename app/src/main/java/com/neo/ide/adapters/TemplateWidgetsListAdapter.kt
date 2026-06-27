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



package com.neo.ide.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.neo.ide.adapters.TemplateWidgetsListAdapter.WidgetViewHolder
import com.neo.ide.databinding.LayoutTemplateWidgetlistItemBinding
import com.neo.ide.templates.ITemplateWidgetViewProvider
import com.neo.ide.templates.Widget

/**
 * A [RecyclerView.Adapter] that is used to show the widgets from templates.
 *
 * @author Akash Yadav
 */
class TemplateWidgetsListAdapter(private val widgets: List<Widget<*>>) :
  RecyclerView.Adapter<WidgetViewHolder>() {

  class WidgetViewHolder(
    internal val binding: LayoutTemplateWidgetlistItemBinding
  ) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
  ): WidgetViewHolder {
    return WidgetViewHolder(LayoutTemplateWidgetlistItemBinding.inflate(
      LayoutInflater.from(parent.context), parent, false))
  }

  override fun getItemCount(): Int {
    return widgets.size
  }

  override fun onBindViewHolder(holder: WidgetViewHolder, position: Int) {
    holder.binding.apply {
      val viewProvider = ITemplateWidgetViewProvider.getInstance()
      val widget = widgets[position]
      val view = viewProvider.createView(root.context, widget)

      root.removeAllViews()
      root.addView(view,
        LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT))
    }
  }
}
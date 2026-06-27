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
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils
import com.google.android.material.shape.CornerFamily
import com.neo.ide.adapters.TemplateListAdapter.ViewHolder
import com.neo.ide.databinding.LayoutTemplateListItemBinding
import com.neo.ide.templates.Template

/**
 * [RecyclerView.Adapter] for showing templates in a [RecyclerView].
 *
 * @author Akash Yadav
 */
class TemplateListAdapter(
  templates: List<Template<*>>,
  private val onClick: ((Template<*>, ViewHolder) -> Unit)? = null
) : RecyclerView.Adapter<ViewHolder>() {

  private val templates = templates.toMutableList()

  class ViewHolder(internal val binding: LayoutTemplateListItemBinding) :
    RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutTemplateListItemBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    ))
  }

  override fun getItemCount(): Int {
    return templates.size
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.binding.apply {
      val template = templates[position]
      if (template == Template.EMPTY) {
        root.visibility = View.INVISIBLE
        return@apply
      }
      templateName.setText(template.templateName)
      templateIcon.setImageResource(template.thumb)
      templateIcon.shapeAppearanceModel =
        templateIcon.shapeAppearanceModel.toBuilder()
          .setAllCorners(CornerFamily.ROUNDED, ConvertUtils.dp2px(8f).toFloat())
          .build()

      root.setOnClickListener {
        onClick?.invoke(template, holder)
      }
    }
  }

  internal fun fillDiff(extras: Int) {
    val count = itemCount
    for (i in 1..extras) {
      templates.add(Template.EMPTY)
    }

    val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
      override fun getOldListSize(): Int {
        return count
      }

      override fun getNewListSize(): Int {
        return count + extras
      }

      override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newItemPosition < count && oldItemPosition == newItemPosition
      }

      override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame(oldItemPosition, newItemPosition)
      }
    })

    diff.dispatchUpdatesTo(this)
  }
}

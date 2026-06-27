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



package com.neo.ide.adapters.onboarding

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neo.ide.databinding.LayoutOnboardingItemBinding
import com.neo.ide.models.OnboardingItem

/**
 * Default implmentation of [RecyclerView.Adapter] for showing [OnboardingItem]s.
 *
 * @author Akash Yadav
 */
open class DefaultOnboardingItemAdapter<T : OnboardingItem>(
  protected val items: List<T>,
  protected val onItemClickListener: OnItemClickListener<T>? = null,
  protected val onItemLongClickListener: OnItemLongClickListener<T>? = null
) : RecyclerView.Adapter<DefaultOnboardingItemAdapter.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
      LayoutOnboardingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    doBindViewHolder(holder, position, getItem(position), holder.binding)
  }

  protected open fun doBindViewHolder(
    holder: ViewHolder,
    position: Int,
    item: T,
    binding: LayoutOnboardingItemBinding
  ) {
    binding.content.title.text = item.title

    if (item.description.isNotBlank()) {
      binding.content.description.text = item.description
    } else {
      binding.content.description.visibility = View.INVISIBLE
    }

    if (item.icon != 0) {
      binding.content.icon.setImageResource(item.icon)
      if (item.iconTint != 0) {
        binding.content.icon.supportImageTintList = ColorStateList.valueOf(item.iconTint)
      }
    } else {
      binding.content.icon.visibility = View.INVISIBLE
    }

    binding.root.isClickable = item.isClickable
    binding.root.isFocusable = item.isClickable

    if (item.isClickable && onItemClickListener != null) {
      binding.root.setOnClickListener { onItemClickListener.onClick(item, position, binding) }
    }

    if (item.isLongClickable && onItemLongClickListener != null) {
      binding.root.setOnLongClickListener { onItemLongClickListener.onLongClick(item, position, binding) }
    }
  }

  override fun getItemCount(): Int {
    return items.size
  }

  fun getItem(index: Int): T {
    return items[index]
  }

  class ViewHolder(val binding: LayoutOnboardingItemBinding) :
    RecyclerView.ViewHolder(binding.root)

  fun interface OnItemClickListener<T : OnboardingItem> {

    fun onClick(item: T, position: Int, binding: LayoutOnboardingItemBinding)
  }

  fun interface OnItemLongClickListener<T : OnboardingItem> {

    fun onLongClick(item: T, position: Int, binding: LayoutOnboardingItemBinding): Boolean
  }
}
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.neo.ide.R
import com.neo.ide.app.IDEApplication
import com.neo.ide.contributors.Contributor
import com.neo.ide.databinding.LayoutContributorsItemBinding

/**
 * @author Akash Yadav
 */
class ContributorsGridAdapter(
  contributors: List<Contributor>
) : RecyclerView.Adapter<ContributorsGridAdapter.ViewHolder>() {

  private val contributors = contributors.toMutableList()

  class ViewHolder(val binding: LayoutContributorsItemBinding) :
    RecyclerView.ViewHolder(binding.root)

  override fun getItemCount(): Int {
    return contributors.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
      LayoutContributorsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val binding = holder.binding
    val contributor = contributors[position]

    if (contributor is EmptyContributor) {
      binding.root.visibility = View.INVISIBLE
      return
    }

    Glide.with(binding.root)
      .load(contributor.avatarUrl)
      .placeholder(R.drawable.ic_account)
      .transition(DrawableTransitionOptions.withCrossFade(100))
      .into(binding.root)

    binding.root.setOnClickListener {
      IDEApplication.instance.openUrl(contributor.profileUrl)
    }
  }

  object EmptyContributor : Contributor {
    override val id: Int
      get() = 0
    override val username: String
      get() = ""
    override val avatarUrl: String
      get() = ""
    override val profileUrl: String
      get() = ""
  }

  internal fun fillDiff(extras: Int) {
    val count = itemCount
    for (i in 1..extras) {
      contributors.add(EmptyContributor)
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
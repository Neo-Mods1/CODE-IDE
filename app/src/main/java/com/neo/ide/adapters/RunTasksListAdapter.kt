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
import androidx.recyclerview.widget.RecyclerView
import com.neo.ide.adapters.RunTasksListAdapter.VH
import com.neo.ide.databinding.LayoutRunTaskItemBinding
import com.neo.ide.models.Checkable
import com.neo.ide.tooling.api.models.GradleTask

/**
 * Adapter for showing tasks list in [RunTaskDialogFragment]
 * [com.neo.ide.fragments.RunTasksDialogFragment].
 *
 * @author Akash Yadav
 */
class RunTasksListAdapter
@JvmOverloads
constructor(
  tasks: List<Checkable<GradleTask>>,
  val onCheckChanged: (Checkable<GradleTask>) -> Unit = {}
) : FilterableRecyclerViewAdapter<VH, Checkable<GradleTask>>(tasks) {

  data class VH(val binding: LayoutRunTaskItemBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    return VH(LayoutRunTaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  }

  override fun onBindViewHolder(holder: VH, position: Int) {
    val binding = holder.binding
    val data = getItem(position)
    val task = data.data

    binding.check.isChecked = data.isChecked
    binding.taskPath.text = task.path
    binding.taskDesc.text = task.description

    binding.root.setOnClickListener {
      data.isChecked = !data.isChecked
      binding.check.isChecked = data.isChecked
      onCheckChanged(data)
    }
  }

  override fun getQueryCandidate(item: Checkable<GradleTask>): String {
    return item.data.path
  }
}

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
import com.neo.ide.databinding.LayoutMainActionItemBinding
import com.neo.ide.models.MainScreenAction

/**
 * Adapter for the actions available on the main screen.
 *
 * @author Akash Yadav
 */
class MainActionsListAdapter
@JvmOverloads
constructor(val actions: List<MainScreenAction> = emptyList()) :
  RecyclerView.Adapter<MainActionsListAdapter.VH>() {
  class VH(val binding: LayoutMainActionItemBinding) : RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    VH(LayoutMainActionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
  override fun getItemCount(): Int = actions.size

  fun getAction(index: Int) = actions[index]
  
  override fun onBindViewHolder(holder: VH, position: Int) {
    val action = getAction(index = position)
    val binding = holder.binding
    
    binding.root.apply {
      setText(action.text)
      setIconResource(action.icon)
      setOnClickListener {
        action.onClick?.invoke(action, it)
      }
      action.onLongClick?.let { onLongClick ->
        setOnLongClickListener {
          onLongClick(action, it)
        }
      }
    }
  }
}

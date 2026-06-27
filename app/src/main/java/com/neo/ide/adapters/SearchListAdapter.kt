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

/**
 * This file is part of AndroidIDE.
 *
 * AndroidIDE is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * AndroidIDE is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with AndroidIDE. If not,
 * see <https:></https:>//www.gnu.org/licenses/>.
 */
package com.neo.ide.adapters

import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.blankj.utilcode.util.ThreadUtils
import com.neo.ide.R
import com.neo.ide.adapters.SearchListAdapter.VH
import com.neo.ide.databinding.LayoutSearchResultGroupBinding
import com.neo.ide.databinding.LayoutSearchResultItemBinding
import com.neo.ide.models.FileExtension
import com.neo.ide.models.SearchResult
import com.neo.ide.syntax.colorschemes.SchemeAndroidIDE
import com.neo.ide.syntax.highlighters.JavaHighlighter
import com.neo.ide.utils.resolveAttr
import java.io.File
import java.util.concurrent.CompletableFuture

class SearchListAdapter(
  private val results: Map<File, List<SearchResult>?>,
  private val onFileClick: (File) -> Unit,
  private val onMatchClick: (SearchResult) -> Unit,
  private val keys: List<File>
) : Adapter<VH>() {

  constructor(
    results: Map<File, List<SearchResult>?>,
    onFileClick: (File) -> Unit,
    onMatchClick: (SearchResult) -> Unit
  ) : this(results, onFileClick, onMatchClick, results.keys.toList())

  override fun onCreateViewHolder(p1: ViewGroup, p2: Int): VH {
    return VH(LayoutSearchResultGroupBinding.inflate(LayoutInflater.from(p1.context)))
  }

  override fun onBindViewHolder(p1: VH, p2: Int) {
    val binding = p1.binding
    val file = keys[p2]
    val matches = results[file] ?: listOf()
    val color = binding.icon.context.resolveAttr(R.attr.colorPrimary)
    binding.title.text = file.name
    binding.icon.setImageResource(FileExtension.Factory.forFile(file).icon)
    binding.icon.setColorFilter(color, SRC_ATOP)
    binding.items.layoutManager = LinearLayoutManager(binding.items.context)
    binding.items.adapter = ChildAdapter(matches)
    binding.root.setOnClickListener { onFileClick(file) }
  }

  override fun getItemCount(): Int {
    return results.size
  }

  inner class ChildAdapter(val matches: List<SearchResult>) : Adapter<ChildVH>() {

    override fun onCreateViewHolder(p1: ViewGroup, p2: Int): ChildVH {
      return ChildVH(LayoutSearchResultItemBinding.inflate(LayoutInflater.from(p1.context)))
    }

    override fun onBindViewHolder(p1: ChildVH, p2: Int) {
      val match = matches[p2]
      val binding = p1.binding
      CompletableFuture.runAsync {
        try {
          val scheme = SchemeAndroidIDE.newInstance(binding.text.context)
          val sb = JavaHighlighter().highlight(scheme, match.line, match.match)
          ThreadUtils.runOnUiThread { binding.text.text = sb }
        } catch (e: Exception) {
          ThreadUtils.runOnUiThread { binding.text.text = match.match }
        }
      }
      binding.root.setOnClickListener { onMatchClick(match) }
    }

    override fun getItemCount(): Int {
      return matches.size
    }
  }

  class VH(val binding: LayoutSearchResultGroupBinding) : ViewHolder(binding.root)
  class ChildVH(val binding: LayoutSearchResultItemBinding) : ViewHolder(binding.root)
}

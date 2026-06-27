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

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.Callback
import androidx.recyclerview.widget.DiffUtil.DiffResult
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Filterable [RecyclerView.Adapter].
 *
 * @author Akash Yadav
 */
abstract class FilterableRecyclerViewAdapter<V : RecyclerView.ViewHolder, D>(val items: List<D>) :
  RecyclerView.Adapter<V>() {

  protected var filtered: List<D> = mutableListOf<D>().apply { addAll(items) }
  private var filterJob: Job? = null

  /**
   * Filter the list with the given query.
   *
   * @param query The query.
   */
  @SuppressLint("NotifyDataSetChanged")
  fun filter(query: String?) {
    filterJob?.cancel(CancellationException("A new query has been submitted for filtering"))

    val items = this.items
    filterJob = CoroutineScope(Dispatchers.Default).launch {
      val (filtered, result) = doFilter(query?.trim(), items)

      withContext(Dispatchers.Main) {
        val adapter = this@FilterableRecyclerViewAdapter
        if (result == null) {
          adapter.filtered = adapter.items
          notifyDataSetChanged()
          return@withContext
        }

        adapter.filtered = filtered
        result.dispatchUpdatesTo(adapter)
      }
    }
  }

  private fun doFilter(
    query: String?,
    items: List<D>,
  ): Pair<List<D>, DiffResult?> {
    if (query.isNullOrBlank()) {
      return items to null
    }

    val filtered = items.filter {
      onFilter(it, query)
    }

    val result =
      DiffUtil.calculateDiff(
        object : Callback() {
          override fun getOldListSize(): Int {
            return items.size
          }

          override fun getNewListSize(): Int {
            return filtered.size
          }

          override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return items[oldItemPosition] == filtered[newItemPosition]
          }

          override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return items[oldItemPosition] == filtered[newItemPosition]
          }
        }
      )

    return filtered to result
  }

  /** Get the list item at given index. */
  fun getItem(index: Int): D {
    return filtered[index]
  }

  override fun getItemCount(): Int {
    return filtered.size
  }

  /** Get the query candidate for the given list item. */
  abstract fun getQueryCandidate(item: D): String

  /** Called on every item when filtering the data. */
  protected open fun onFilter(item: D, query: String): Boolean {
    return getQueryCandidate(item).contains(query, ignoreCase = true)
  }
}

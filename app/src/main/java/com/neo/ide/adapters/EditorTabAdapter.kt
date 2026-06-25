/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.neo.ide.R
import com.neo.ide.models.EditorTab
import com.neo.ide.models.FileType

class EditorTabAdapter(
    private val onTabClick: (EditorTab) -> Unit,
    private val onTabLongClick: (EditorTab, View) -> Unit,
    private val onTabClose: (EditorTab) -> Unit
) : RecyclerView.Adapter<EditorTabAdapter.TabViewHolder>() {

    private val tabs = mutableListOf<EditorTab>()
    private var activeTabId: String? = null

    fun submitList(newTabs: List<EditorTab>) {
        tabs.clear()
        tabs.addAll(newTabs)
        notifyDataSetChanged()
    }

    fun setActiveTab(tabId: String?) {
        val oldActiveId = activeTabId
        activeTabId = tabId

        oldActiveId?.let { oldId ->
            val oldPos = tabs.indexOfFirst { it.id == oldId }
            if (oldPos != -1) notifyItemChanged(oldPos)
        }

        tabId?.let { newId ->
            val newPos = tabs.indexOfFirst { it.id == newId }
            if (newPos != -1) notifyItemChanged(newPos)
        }
    }

    fun addTab(tab: EditorTab) {
        tabs.add(tab)
        notifyItemInserted(tabs.size - 1)
    }

    fun removeTab(tab: EditorTab) {
        val position = tabs.indexOf(tab)
        if (position != -1) {
            tabs.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateTabDirty(tabId: String, isDirty: Boolean) {
        val position = tabs.indexOfFirst { it.id == tabId }
        if (position != -1) {
            tabs[position].isDirty = isDirty
            notifyItemChanged(position)
        }
    }

    fun getTabAt(position: Int): EditorTab? {
        return tabs.getOrNull(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_editor_tab, parent, false)
        return TabViewHolder(view)
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        val tab = tabs[position]
        holder.bind(tab, tab.id == activeTabId)
    }

    override fun getItemCount(): Int = tabs.size

    inner class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tabIcon: ImageView = itemView.findViewById(R.id.tab_icon)
        private val tabTitle: TextView = itemView.findViewById(R.id.tab_title)
        private val tabDirty: View = itemView.findViewById(R.id.tab_dirty)
        private val tabClose: ImageView = itemView.findViewById(R.id.tab_close)

        fun bind(tab: EditorTab, isActive: Boolean) {
            val context = itemView.context

            // Set icon
            tabIcon.setImageResource(getFileIconRes(tab.fileType))

            // Set title
            tabTitle.text = tab.fileName

            // Set colors based on active state
            if (isActive) {
                tabTitle.setTextColor(
                    ContextCompat.getColor(context, R.color.tab_text_active)
                )
                itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.tab_active)
                )
            } else {
                tabTitle.setTextColor(
                    ContextCompat.getColor(context, R.color.tab_text_inactive)
                )
                itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.tab_inactive)
                )
            }

            // Show dirty indicator
            tabDirty.visibility = if (tab.isDirty) View.VISIBLE else View.GONE

            // Show close button on active tab
            tabClose.visibility = if (isActive) View.VISIBLE else View.GONE

            // Click listeners
            itemView.setOnClickListener { onTabClick(tab) }

            itemView.setOnLongClickListener { view ->
                onTabLongClick(tab, view)
                true
            }

            tabClose.setOnClickListener { onTabClose(tab) }
        }

        private fun getFileIconRes(fileType: FileType): Int {
            return when (fileType) {
                FileType.KOTLIN -> R.drawable.ic_kotlin
                FileType.JAVA -> R.drawable.ic_java
                FileType.XML -> R.drawable.ic_code
                FileType.GRADLE -> R.drawable.ic_gradle
                FileType.KTS -> R.drawable.ic_gradle_kts
                FileType.JSON -> R.drawable.ic_code
                FileType.MARKDOWN -> R.drawable.ic_text
                FileType.PROPERTIES -> R.drawable.ic_text
                FileType.PNG, FileType.JPG, FileType.JPEG, FileType.GIF, FileType.WEBP, FileType.SVG -> {
                    R.drawable.ic_image
                }
                FileType.TXT -> R.drawable.ic_text
                FileType.SH -> R.drawable.ic_shell
                FileType.PYTHON -> R.drawable.ic_python
                FileType.CPP, FileType.C, FileType.H -> R.drawable.ic_cpp
                FileType.FOLDER -> R.drawable.ic_folder
                FileType.FILE -> R.drawable.ic_file
            }
        }
    }
}

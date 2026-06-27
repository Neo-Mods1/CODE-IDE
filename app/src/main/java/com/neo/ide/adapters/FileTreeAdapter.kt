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
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.neo.ide.R
import com.neo.ide.models.FileNode
import com.neo.ide.models.FileType

class FileTreeAdapter(
    private val onFileClick: (FileNode) -> Unit,
    private val onFileLongClick: (FileNode) -> Unit,
    private val onFolderToggle: (FileNode) -> Unit
) : RecyclerView.Adapter<FileTreeAdapter.FileTreeViewHolder>() {

    private val items = mutableListOf<FileNode>()
    private var selectedFile: FileNode? = null

    fun submitList(newItems: List<FileNode>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun setSelectedFile(file: FileNode?) {
        val oldSelected = selectedFile
        selectedFile = file
        
        oldSelected?.let { old ->
            val oldPos = items.indexOf(old)
            if (oldPos != -1) notifyItemChanged(oldPos)
        }
        
        file?.let { new ->
            val newPos = items.indexOf(new)
            if (newPos != -1) notifyItemChanged(newPos)
        }
    }

    fun toggleFolder(fileNode: FileNode) {
        val position = items.indexOf(fileNode)
        if (position != -1) {
            fileNode.isExpanded = !fileNode.isExpanded
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileTreeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file_tree, parent, false)
        return FileTreeViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileTreeViewHolder, position: Int) {
        val fileNode = items[position]
        holder.bind(fileNode, fileNode == selectedFile)
    }

    override fun getItemCount(): Int = items.size

    inner class FileTreeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val indentSpacer: View = itemView.findViewById(R.id.indent_spacer)
        private val expandIcon: ImageView = itemView.findViewById(R.id.expand_icon)
        private val fileIcon: ImageView = itemView.findViewById(R.id.file_icon)
        private val fileName: TextView = itemView.findViewById(R.id.file_name)
        private val dirtyIndicator: View = itemView.findViewById(R.id.dirty_indicator)

        fun bind(fileNode: FileNode, isSelected: Boolean) {
            val context = itemView.context
            
            // Set indent
            val indentPx = (fileNode.depth * context.resources.getDimension(R.dimen.drawer_indent_size)).toInt()
            val params = indentSpacer.layoutParams as ViewGroup.MarginLayoutParams
            params.width = indentPx
            indentSpacer.layoutParams = params

            // Set expand icon
            if (fileNode.isDirectory) {
                expandIcon.visibility = View.VISIBLE
                expandIcon.setImageResource(
                    if (fileNode.isExpanded) android.R.drawable.arrow_up_float
                    else android.R.drawable.arrow_down_float
                )
                expandIcon.rotation = if (fileNode.isExpanded) 0f else -90f
            } else {
                expandIcon.visibility = View.INVISIBLE
            }

            // Set file icon
            val iconRes = getFileIcon(fileNode)
            fileIcon.setImageResource(iconRes)

            // Set file name
            fileName.text = fileNode.name

            // Set selection highlight
            if (isSelected) {
                itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.drawer_item_selected)
                )
            } else {
                itemView.background = null
            }

            // Set click listeners
            itemView.setOnClickListener {
                if (fileNode.isDirectory) {
                    onFolderToggle(fileNode)
                } else {
                    onFileClick(fileNode)
                }
            }

            itemView.setOnLongClickListener {
                onFileLongClick(fileNode)
                true
            }
        }

        private fun getFileIcon(fileNode: FileNode): Int {
            if (fileNode.isDirectory) {
                return if (fileNode.isExpanded) {
                    android.R.drawable.ic_menu_sort_by_size
                } else {
                    android.R.drawable.ic_menu_agenda
                }
            }

            return when (fileNode.getFileType()) {
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

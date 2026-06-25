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
                FileType.KOTLIN -> android.R.drawable.ic_menu_edit
                FileType.JAVA -> android.R.drawable.ic_menu_edit
                FileType.XML -> android.R.drawable.ic_menu_edit
                FileType.GRADLE -> android.R.drawable.ic_menu_save
                FileType.KTS -> android.R.drawable.ic_menu_save
                FileType.JSON -> android.R.drawable.ic_menu_info_details
                FileType.MARKDOWN -> android.R.drawable.ic_menu_info_details
                FileType.PROPERTIES -> android.R.drawable.ic_menu_info_details
                FileType.PNG, FileType.JPG, FileType.JPEG, FileType.GIF, FileType.WEBP, FileType.SVG -> {
                    android.R.drawable.ic_menu_gallery
                }
                FileType.TXT -> android.R.drawable.ic_menu_edit
                FileType.SH -> android.R.drawable.ic_menu_edit
                FileType.PYTHON -> android.R.drawable.ic_menu_edit
                FileType.CPP, FileType.C, FileType.H -> android.R.drawable.ic_menu_edit
                FileType.FOLDER -> android.R.drawable.ic_menu_agenda
                FileType.FILE -> android.R.drawable.ic_menu_save
            }
        }
    }
}

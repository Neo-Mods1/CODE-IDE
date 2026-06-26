/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.templates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neo.ide.R

class TemplateListAdapter(
    private val templates: List<ProjectTemplate>,
    private val onClick: (ProjectTemplate) -> Unit
) : RecyclerView.Adapter<TemplateListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.template_icon)
        val name: TextView = view.findViewById(R.id.template_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_template, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val template = templates[position]
        holder.icon.setImageResource(template.iconRes)
        holder.name.setText(template.nameRes)
        holder.itemView.setOnClickListener { onClick(template) }
    }

    override fun getItemCount(): Int = templates.size
}

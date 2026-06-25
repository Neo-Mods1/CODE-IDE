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
import androidx.recyclerview.widget.RecyclerView
import com.neo.ide.R
import com.neo.ide.models.HomeScreenAction

class HomeActionsAdapter(
    private val actions: List<HomeScreenAction>
) : RecyclerView.Adapter<HomeActionsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.action_icon)
        val text: TextView = view.findViewById(R.id.action_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_action, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val action = actions[position]
        holder.icon.setImageResource(action.icon)
        holder.text.setText(action.text)
        holder.itemView.setOnClickListener { action.onClick?.invoke(action) }
    }

    override fun getItemCount(): Int = actions.size
}

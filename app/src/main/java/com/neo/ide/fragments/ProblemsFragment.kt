/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.neo.ide.R

class ProblemsFragment : Fragment() {

    private var contentTextView: TextView? = null
    private val problemEntries = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_panel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentTextView = view.findViewById(R.id.panel_content)
        updateContent()
    }

    fun addProblem(message: String) {
        problemEntries.add(message)
        updateContent()
    }

    fun clearProblems() {
        problemEntries.clear()
        updateContent()
    }

    private fun updateContent() {
        contentTextView?.text = if (problemEntries.isEmpty()) {
            "No problems"
        } else {
            problemEntries.joinToString("\n")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        contentTextView = null
    }

    companion object {
        fun newInstance(): ProblemsFragment {
            return ProblemsFragment()
        }
    }
}

package com.neo.ide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.neo.ide.R

class BuildOutputFragment : Fragment() {

    private var contentTextView: TextView? = null
    private val logEntries = mutableListOf<String>()

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

    fun appendLog(message: String) {
        logEntries.add(message)
        updateContent()
    }

    fun clearLogs() {
        logEntries.clear()
        updateContent()
    }

    private fun updateContent() {
        contentTextView?.text = if (logEntries.isEmpty()) {
            getString(R.string.panel_no_output)
        } else {
            logEntries.joinToString("\n")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        contentTextView = null
    }

    companion object {
        fun newInstance(): BuildOutputFragment {
            return BuildOutputFragment()
        }
    }
}

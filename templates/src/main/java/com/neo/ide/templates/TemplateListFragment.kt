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

package com.neo.ide.templates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neo.ide.templates.R

class TemplateListFragment : Fragment() {

    private var templateList: RecyclerView? = null
    private var onTemplateSelected: ((ProjectTemplate) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_template_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        templateList = view.findViewById(R.id.template_list)
        val exitButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.exit_button)

        templateList?.layoutManager = GridLayoutManager(requireContext(), 3)

        val templates = ProjectTemplate.defaults()
        templateList?.adapter = TemplateListAdapter(templates) { template ->
            onTemplateSelected?.invoke(template)
        }

        exitButton.setOnClickListener {
            requireActivity().finish()
        }
    }

    fun setOnTemplateSelectedListener(listener: (ProjectTemplate) -> Unit) {
        onTemplateSelected = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        templateList = null
    }
}

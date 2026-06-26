/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.templates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neo.ide.R

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

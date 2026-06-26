/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.templates

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.neo.ide.R

class TemplateWidgetsAdapter(
    private val widgets: List<TemplateWidget>,
    private val onValueChanged: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_TEXT = 0
        const val TYPE_SPINNER = 1
        const val TYPE_CHECKBOX = 2
    }

    sealed class TemplateWidget {
        data class TextField(
            val label: String,
            val value: String,
            val editable: Boolean = true,
            val endIcon: Int = 0,
            val onValueChanged: ((String) -> Unit)? = null
        ) : TemplateWidget()

        data class Spinner(
            val label: String,
            val items: List<String>,
            val selectedIndex: Int = 0,
            val onItemSelected: ((Int) -> Unit)? = null
        ) : TemplateWidget()

        data class CheckBox(
            val label: String,
            val checked: Boolean = false,
            val onCheckedChanged: ((Boolean) -> Unit)? = null
        ) : TemplateWidget()
    }

    override fun getItemViewType(position: Int): Int = when (widgets[position]) {
        is TemplateWidget.TextField -> TYPE_TEXT
        is TemplateWidget.Spinner -> TYPE_SPINNER
        is TemplateWidget.CheckBox -> TYPE_CHECKBOX
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_TEXT -> {
                val view = inflater.inflate(R.layout.item_widget_text, parent, false)
                TextViewHolder(view)
            }
            TYPE_SPINNER -> {
                val view = inflater.inflate(R.layout.item_widget_spinner, parent, false)
                SpinnerViewHolder(view)
            }
            TYPE_CHECKBOX -> {
                val view = inflater.inflate(R.layout.item_widget_checkbox, parent, false)
                CheckBoxViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val widget = widgets[position]) {
            is TemplateWidget.TextField -> (holder as TextViewHolder).bind(widget)
            is TemplateWidget.Spinner -> (holder as SpinnerViewHolder).bind(widget)
            is TemplateWidget.CheckBox -> (holder as CheckBoxViewHolder).bind(widget)
        }
    }

    override fun getItemCount(): Int = widgets.size

    inner class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val layout: TextInputLayout = view.findViewById(R.id.layout)
        private val input: TextInputEditText = view.findViewById(R.id.input)

        fun bind(widget: TemplateWidget.TextField) {
            layout.hint = widget.label
            input.setText(widget.value)
            input.isEnabled = widget.editable

            if (widget.endIcon != 0) {
                layout.isEndIconVisible = true
                layout.setEndIconDrawable(widget.endIcon)
            }

            input.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    widget.onValueChanged?.invoke(s?.toString() ?: "")
                    onValueChanged()
                }
            })
        }
    }

    inner class SpinnerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val layout: TextInputLayout = view.findViewById(R.id.layout)
        private val input: AutoCompleteTextView = view.findViewById(R.id.input)

        fun bind(widget: TemplateWidget.Spinner) {
            layout.hint = widget.label
            val adapter = ArrayAdapter(
                itemView.context,
                android.R.layout.simple_dropdown_item_1line,
                widget.items
            )
            input.setAdapter(adapter)
            input.setText(widget.items.getOrElse(widget.selectedIndex) { "" }, false)

            input.setOnItemClickListener { _, _, position, _ ->
                widget.onItemSelected?.invoke(position)
                onValueChanged()
            }
        }
    }

    inner class CheckBoxViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val checkBox: CheckBox = view as CheckBox

        fun bind(widget: TemplateWidget.CheckBox) {
            checkBox.text = widget.label
            checkBox.isChecked = widget.checked
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                widget.onCheckedChanged?.invoke(isChecked)
                onValueChanged()
            }
        }
    }
}

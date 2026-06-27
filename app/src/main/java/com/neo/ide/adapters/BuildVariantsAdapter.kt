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
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.neo.ide.R
import com.neo.ide.databinding.LayoutBuildVariantItemBinding
import com.neo.ide.tooling.api.IAndroidProject
import com.neo.ide.tooling.api.models.BuildVariantInfo
import com.neo.ide.tooling.api.models.BuildVariantInfo.Companion.withSelection
import com.neo.ide.viewmodel.BuildVariantsViewModel
import java.util.Objects

/**
 * [RecyclerView] adapter for showing the list of Android modules and their selected build variant.
 *
 * @property items
 * @author Akash Yadav
 */
class BuildVariantsAdapter(
  private val viewModel: BuildVariantsViewModel,
  private var items: List<BuildVariantInfo>
) : RecyclerView.Adapter<BuildVariantsAdapter.ViewHolder>() {

  class ViewHolder(internal val binding: LayoutBuildVariantItemBinding) :
    RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val binding = LayoutBuildVariantItemBinding.inflate(LayoutInflater.from(parent.context), parent,
      false)
    return ViewHolder(binding)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val binding = holder.binding
    val variantInfo = items[position]

    binding.moduleName.text = variantInfo.projectPath

    binding.variantName.apply {

      val viewModel = viewModel

      setAdapter(
        ArrayAdapter(binding.root.context, R.layout.support_simple_spinner_dropdown_item,
          variantInfo.buildVariants
        )
      )

      var listSelection = variantInfo.buildVariants.indexOf(variantInfo.selectedVariant)
      if (listSelection < 0 || listSelection >= variantInfo.buildVariants.size) {
        listSelection = 0
      }

      this.listSelection = listSelection
      setText(variantInfo.selectedVariant, false)

      addTextChangedListener { editable ->
        // update the changed build variants map
        viewModel.updatedBuildVariants = viewModel.updatedBuildVariants.also { variants ->

          // the newly selected build variant
          // if this is different that the variant that was used while initializing the project,
          // then the user is notified to re-sync the project
          // else the selection is cleared
          val newSelection = editable?.toString() ?: IAndroidProject.DEFAULT_VARIANT

          if (!Objects.equals(variantInfo.selectedVariant, newSelection)) {
            variants[variantInfo.projectPath] = variantInfo.withSelection(newSelection)
          } else {
            variants.remove(variantInfo.projectPath)
          }
        }
      }
    }
  }
}
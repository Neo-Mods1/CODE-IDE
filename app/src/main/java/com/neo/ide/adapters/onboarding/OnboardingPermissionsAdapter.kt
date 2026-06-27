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



package com.neo.ide.adapters.onboarding

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.google.android.material.button.MaterialButton
import com.neo.ide.R
import com.neo.ide.databinding.LayoutOnboardingPermissionItemBinding
import com.neo.ide.models.OnboardingPermissionItem

/**
 * @author Akash Yadav
 */
class OnboardingPermissionsAdapter(private val permissions: List<OnboardingPermissionItem>,
  private val requestPermission: (String) -> Unit) :
  RecyclerView.Adapter<OnboardingPermissionsAdapter.ViewHolder>() {

  class ViewHolder(val binding: LayoutOnboardingPermissionItemBinding) :
    RecyclerView.ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
      LayoutOnboardingPermissionItemBinding.inflate(LayoutInflater.from(parent.context), parent,
        false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val binding = holder.binding
    val permission = permissions[position]

    binding.infoContent.apply {
      title.setText(permission.title)
      description.setText(permission.description)
    }

    binding.grantButton.setOnClickListener {
      requestPermission(permission.permission)
    }

    if (permission.isGranted) {
      binding.grantButton.apply {
        isEnabled = false
        text = ""
        icon = ContextCompat.getDrawable(binding.root.context, R.drawable.ic_ok)
        iconTint = ColorStateList.valueOf(
          ContextCompat.getColor(binding.root.context, R.color.green_500))
        iconGravity = MaterialButton.ICON_GRAVITY_TEXT_TOP
        iconPadding = 0
        iconSize = SizeUtils.dp2px(28f)
      }
    }
  }

  override fun getItemCount(): Int {
    return permissions.size
  }
}
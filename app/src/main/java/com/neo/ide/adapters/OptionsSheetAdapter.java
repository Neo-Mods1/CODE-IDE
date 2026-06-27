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

/************************************************************************************
 * This file is part of AndroidIDE.
 *
 *
 *
 * AndroidIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AndroidIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 *
 **************************************************************************************/

package com.neo.ide.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.neo.ide.databinding.LayoutOptionssheetItemBinding;
import com.neo.ide.models.SheetOption;
import java.util.List;

public class OptionsSheetAdapter extends RecyclerView.Adapter<OptionsSheetAdapter.VH> {

  private OnOptionsClickListener listener;
  private List<SheetOption> options;

  public OptionsSheetAdapter(List<SheetOption> options, OnOptionsClickListener listener) {
    this.options = options;
    this.listener = listener;
  }

  @Override
  public OptionsSheetAdapter.VH onCreateViewHolder(ViewGroup p1, int p2) {
    return new VH(
        LayoutOptionssheetItemBinding.inflate(LayoutInflater.from(p1.getContext()), p1, false));
  }

  @Override
  public void onBindViewHolder(OptionsSheetAdapter.VH p1, int p2) {
    final LayoutOptionssheetItemBinding binding = p1.binding;
    final SheetOption option = options.get(p2);

    binding.text.setText(option.title);
    binding.icon.setImageDrawable(option.icon);

    binding
        .getRoot()
        .setOnClickListener(
            v -> {
              if (listener != null) listener.onOptionClick(option);
            });
  }

  @Override
  public int getItemCount() {
    return options.size();
  }

  public class VH extends RecyclerView.ViewHolder {
    private LayoutOptionssheetItemBinding binding;

    public VH(LayoutOptionssheetItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }
  }

  public static interface OnOptionsClickListener {
    public void onOptionClick(SheetOption option);
  }
}

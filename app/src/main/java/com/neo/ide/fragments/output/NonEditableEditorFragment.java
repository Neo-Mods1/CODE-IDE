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



package com.neo.ide.fragments.output;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.neo.ide.R;
import com.neo.ide.databinding.FragmentNonEditableEditorBinding;
import com.neo.ide.editor.ui.IDEEditor;
import com.neo.ide.fragments.EmptyStateFragment;
import com.neo.ide.syntax.colorschemes.SchemeAndroidIDE;
import com.neo.ide.utils.TypefaceUtilsKt;
import io.github.rosemoe.sora.lang.EmptyLanguage;

public abstract class NonEditableEditorFragment extends
    EmptyStateFragment<FragmentNonEditableEditorBinding>
    implements ShareableOutputFragment {

  public NonEditableEditorFragment() {
    super(R.layout.fragment_non_editable_editor, FragmentNonEditableEditorBinding::bind);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getEmptyStateViewModel().getEmptyMessage().setValue(createEmptyStateMessage());
    final var editor = getBinding().getRoot();
    editor.setEditable(false);
    editor.setDividerWidth(0);
    editor.setEditorLanguage(new EmptyLanguage());
    editor.setWordwrap(false);
    editor.setUndoEnabled(false);
    editor.setTypefaceLineNumber(TypefaceUtilsKt.jetbrainsMono());
    editor.setTypefaceText(TypefaceUtilsKt.jetbrainsMono());
    editor.setTextSize(12);
    editor.setColorScheme(SchemeAndroidIDE.newInstance(requireContext()));
  }

  private CharSequence createEmptyStateMessage() {
    return null;
  }

  @NonNull
  @Override
  public String getContent() {
    final var editor = getEditor();
    if (editor == null) {
      return "";
    }

    return editor.getText().toString();
  }

  @Nullable
  public IDEEditor getEditor() {
    final var binding = get_binding();
    if (binding == null) {
      return null;
    }
    return binding.editor;
  }

  @NonNull
  @Override
  public String getFilename() {
    return "build_output";
  }

  @Override
  public void clearOutput() {
    final var editor = getEditor();
    if (editor == null) {
      return;
    }

    // Editing CodeEditor's content is a synchronized operation
    editor.getText().delete(0, editor.getText().length());
    getEmptyStateViewModel().isEmpty().setValue(true);
  }
}

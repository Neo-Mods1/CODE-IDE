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



package com.neo.ide.lsp.java.rewrite;

import androidx.annotation.NonNull;
import com.neo.ide.lsp.java.compiler.CompilerProvider;
import com.neo.ide.lsp.java.parser.ParseTask;
import com.neo.ide.lsp.java.utils.EditHelper;
import com.neo.ide.lsp.java.visitors.FindTypeDeclarationAt;
import com.neo.ide.lsp.models.TextEdit;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import openjdk.source.tree.ClassTree;

public class RemoveClass extends Rewrite {
  final Path file;
  final int position;

  public RemoveClass(Path file, int position) {
    this.file = file;
    this.position = position;
  }

  @NonNull
  @Override
  public Map<Path, TextEdit[]> rewrite(@NonNull CompilerProvider compiler) {
    ParseTask task = compiler.parse(file);
    final ClassTree type = new FindTypeDeclarationAt(task.task).scan(task.root, (long) position);
    TextEdit[] edits = {EditHelper.removeTree(task.task, task.root, type)};
    return Collections.singletonMap(file, edits);
  }
}

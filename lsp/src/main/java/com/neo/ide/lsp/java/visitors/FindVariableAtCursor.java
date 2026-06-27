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



package com.neo.ide.lsp.java.visitors;

import openjdk.source.tree.CompilationUnitTree;
import openjdk.source.tree.VariableTree;
import openjdk.source.util.JavacTask;
import openjdk.source.util.SourcePositions;
import openjdk.source.util.TreeScanner;
import openjdk.source.util.Trees;

public class FindVariableAtCursor extends TreeScanner<VariableTree, Integer> {
  private final SourcePositions pos;
  private CompilationUnitTree root;

  public FindVariableAtCursor(JavacTask task) {
    pos = Trees.instance(task).getSourcePositions();
  }

  @Override
  public VariableTree reduce(VariableTree r1, VariableTree r2) {
    if (r1 != null) return r1;
    return r2;
  }

  @Override
  public VariableTree visitCompilationUnit(CompilationUnitTree t, Integer find) {
    root = t;
    return super.visitCompilationUnit(t, find);
  }

  @Override
  public VariableTree visitVariable(VariableTree t, Integer find) {
    VariableTree smaller = super.visitVariable(t, find);
    if (smaller != null) {
      return smaller;
    }
    if (pos.getStartPosition(root, t) <= find && find < pos.getEndPosition(root, t)) {
      return t;
    }
    return null;
  }
}

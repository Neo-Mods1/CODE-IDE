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

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import openjdk.source.tree.CompilationUnitTree;
import openjdk.source.tree.VariableTree;
import openjdk.source.util.JavacTask;
import openjdk.source.util.SourcePositions;
import openjdk.source.util.TreePath;
import openjdk.source.util.TreePathScanner;
import openjdk.source.util.Trees;

/**
 * Finds variables between the given start and end indexes.
 *
 * @author Akash Yadav
 */
public class FindVariablesBetween extends TreePathScanner<Void, Void> {

  private final long start;
  private final long end;
  private final SourcePositions positions;
  private final List<TreePath> paths = new ArrayList<>();
  private CompilationUnitTree root;

  public FindVariablesBetween(@NonNull JavacTask task, long start, long end) {
    Trees trees = Trees.instance(task);
    this.positions = trees.getSourcePositions();
    this.start = start;
    this.end = end;
  }

  @Override
  public Void visitCompilationUnit(CompilationUnitTree node, Void unused) {
    this.root = node;
    return super.visitCompilationUnit(node, unused);
  }

  @Override
  public Void visitVariable(VariableTree node, Void unused) {
    if (isInRange(node)) {
      this.paths.add(getCurrentPath());
    }

    return super.visitVariable(node, unused);
  }

  private boolean isInRange(VariableTree node) {
    final long start = this.positions.getStartPosition(root, node);
    final long end = this.positions.getEndPosition(root, node);
    return (this.start <= start && end <= this.end) || (start <= this.start && end >= this.end);
  }

  @NonNull
  public List<TreePath> getPaths() {
    return paths;
  }
}

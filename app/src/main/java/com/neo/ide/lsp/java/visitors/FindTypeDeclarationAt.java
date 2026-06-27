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

import openjdk.source.tree.ClassTree;
import openjdk.source.tree.CompilationUnitTree;
import openjdk.source.util.JavacTask;
import openjdk.source.util.SourcePositions;
import openjdk.source.util.TreePath;
import openjdk.source.util.TreePathScanner;
import openjdk.source.util.Trees;

public class FindTypeDeclarationAt extends TreePathScanner<ClassTree, Long> {
  private final SourcePositions pos;
  private CompilationUnitTree root;

  private TreePath path;

  public FindTypeDeclarationAt(JavacTask task) {
    pos = Trees.instance(task).getSourcePositions();
  }

  @Override
  public ClassTree reduce(ClassTree a, ClassTree b) {
    if (a != null) return a;
    return b;
  }

  @Override
  public ClassTree visitCompilationUnit(CompilationUnitTree t, Long find) {
    root = t;
    return super.visitCompilationUnit(t, find);
  }

  @Override
  public ClassTree visitClass(ClassTree t, Long find) {
    ClassTree smaller = super.visitClass(t, find);
    if (smaller != null) {
      return smaller;
    }
    if (pos.getStartPosition(root, t) <= find && find < pos.getEndPosition(root, t)) {
      this.path = getCurrentPath();
      return t;
    }
    return null;
  }

  public TreePath getPath() {
    return path;
  }
}

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
import openjdk.source.tree.NewClassTree;
import openjdk.source.util.JavacTask;
import openjdk.source.util.SourcePositions;
import openjdk.source.util.TreePath;
import openjdk.source.util.TreePathScanner;
import openjdk.source.util.Trees;

/**
 * @author Akash Yadav
 */
public class FindAnonymousTypeDeclaration extends TreePathScanner<ClassTree, Long> {

  private final SourcePositions pos;
  private final CompilationUnitTree root;
  private TreePath stored;

  public FindAnonymousTypeDeclaration(JavacTask task, CompilationUnitTree root) {
    this.pos = Trees.instance(task).getSourcePositions();
    this.root = root;
  }

  @Override
  public ClassTree reduce(ClassTree a, ClassTree b) {
    if (a != null) return a;
    return b;
  }

  @Override
  public ClassTree visitNewClass(NewClassTree t, Long find) {

    if (pos == null) {
      return null;
    }

    ClassTree smaller = super.visitNewClass(t, find);
    if (smaller != null) {
      return smaller;
    }

    if (pos.getStartPosition(root, t.getClassBody()) <= find
        && find < pos.getEndPosition(root, t.getClassBody())) {
      stored = getCurrentPath();
      return t.getClassBody();
    }

    return null;
  }

  public TreePath getStoredPath() {
    return stored;
  }
}

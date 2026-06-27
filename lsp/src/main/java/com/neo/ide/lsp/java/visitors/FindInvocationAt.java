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

import com.neo.ide.progress.ICancelChecker;
import openjdk.source.tree.CompilationUnitTree;
import openjdk.source.tree.MethodInvocationTree;
import openjdk.source.tree.NewClassTree;
import openjdk.source.util.JavacTask;
import openjdk.source.util.SourcePositions;
import openjdk.source.util.TreePath;
import openjdk.source.util.TreePathScanner;
import openjdk.source.util.Trees;

public class FindInvocationAt extends TreePathScanner<TreePath, Long> {

  private final JavacTask task;
  private final ICancelChecker cancelChecker;
  private CompilationUnitTree root;

  public FindInvocationAt(JavacTask task, ICancelChecker cancelChecker) {
    this.task = task;
    this.cancelChecker = cancelChecker;
  }

  @Override
  public TreePath visitCompilationUnit(CompilationUnitTree t, Long find) {
    cancelChecker.abortIfCancelled();
    root = t;
    return reduce(super.visitCompilationUnit(t, find), getCurrentPath());
  }

  @Override
  public TreePath visitMethodInvocation(MethodInvocationTree t, Long find) {
    cancelChecker.abortIfCancelled();
    SourcePositions pos = Trees.instance(task).getSourcePositions();
    long start = pos.getEndPosition(root, t.getMethodSelect()) + 1;
    long end = pos.getEndPosition(root, t) - 1;
    if (start <= find && find <= end) {
      return reduce(super.visitMethodInvocation(t, find), getCurrentPath());
    }
    return super.visitMethodInvocation(t, find);
  }

  @Override
  public TreePath visitNewClass(NewClassTree t, Long find) {
    cancelChecker.abortIfCancelled();
    SourcePositions pos = Trees.instance(task).getSourcePositions();
    long start = pos.getEndPosition(root, t.getIdentifier()) + 1;
    long end = pos.getEndPosition(root, t) - 1;
    if (start <= find && find <= end) {
      return reduce(super.visitNewClass(t, find), getCurrentPath());
    }
    return super.visitNewClass(t, find);
  }

  @Override
  public TreePath reduce(TreePath a, TreePath b) {
    cancelChecker.abortIfCancelled();
    if (a != null) {
      return a;
    }
    return b;
  }
}

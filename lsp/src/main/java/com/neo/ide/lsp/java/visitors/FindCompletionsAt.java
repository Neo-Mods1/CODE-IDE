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

import openjdk.source.tree.CaseTree;
import openjdk.source.tree.CompilationUnitTree;
import openjdk.source.tree.ErroneousTree;
import openjdk.source.tree.IdentifierTree;
import openjdk.source.tree.ImportTree;
import openjdk.source.tree.MemberReferenceTree;
import openjdk.source.tree.MemberSelectTree;
import openjdk.source.tree.Tree;
import openjdk.source.util.JavacTask;
import openjdk.source.util.SourcePositions;
import openjdk.source.util.TreePath;
import openjdk.source.util.TreePathScanner;
import openjdk.source.util.Trees;

public class FindCompletionsAt extends TreePathScanner<TreePath, Long> {

  //  private static final ILogger LOG = ILogger.newInstance("FindCompletionsAt");
  private final JavacTask task;
  private CompilationUnitTree root;

  public FindCompletionsAt(JavacTask task) {
    this.task = task;
  }

  @Override
  public TreePath visitCompilationUnit(CompilationUnitTree t, Long find) {
    root = t;
    return reduce(super.visitCompilationUnit(t, find), getCurrentPath());
  }

  @Override
  public TreePath visitIdentifier(IdentifierTree t, Long find) {
    SourcePositions pos = Trees.instance(task).getSourcePositions();
    long start = pos.getStartPosition(root, t);
    long end = pos.getEndPosition(root, t);
    if (start <= find && find <= end) {
      return getCurrentPath();
    }
    return super.visitIdentifier(t, find);
  }

  @Override
  public TreePath visitMemberSelect(MemberSelectTree t, Long find) {
    SourcePositions pos = Trees.instance(task).getSourcePositions();
    long start = pos.getEndPosition(root, t.getExpression()) + 1;
    long end = pos.getEndPosition(root, t);
    if (start <= find && find <= end) {
      return getCurrentPath();
    }
    return super.visitMemberSelect(t, find);
  }

  @Override
  public TreePath visitMemberReference(MemberReferenceTree t, Long find) {
    SourcePositions pos = Trees.instance(task).getSourcePositions();
    long start = pos.getEndPosition(root, t.getQualifierExpression()) + 2;
    long end = pos.getEndPosition(root, t);
    if (start <= find && find <= end) {
      return getCurrentPath();
    }
    return super.visitMemberReference(t, find);
  }

  @Override
  public TreePath visitCase(CaseTree t, Long find) {
    SourcePositions pos = Trees.instance(task).getSourcePositions();

    // check if the cursor is in the case expression
    // default statements have null expression
    // In case of an identifier tree, we have to check for both, variables and switch constants
    // in
    // CompletionProvider
    if (t.getExpression() != null && !(t.getExpression() instanceof IdentifierTree)) {
      long start = pos.getStartPosition(root, t.getExpression());
      long end = pos.getEndPosition(root, t.getExpression());
      if (start <= find && find <= end) {
        return new TreePath(getCurrentPath(), t.getExpression());
      }
    }

    long start = pos.getStartPosition(root, t) + "case".length();
    long end = pos.getEndPosition(root, t.getExpression());
    if (start <= find && find <= end) {
      return getCurrentPath().getParentPath();
    }

    return super.visitCase(t, find);
  }

  @Override
  public TreePath visitImport(ImportTree t, Long find) {
    SourcePositions pos = Trees.instance(task).getSourcePositions();
    long start = pos.getStartPosition(root, t.getQualifiedIdentifier());
    long end = pos.getEndPosition(root, t.getQualifiedIdentifier());
    if (start <= find && find <= end) {
      return getCurrentPath();
    }
    return super.visitImport(t, find);
  }

  @Override
  public TreePath visitErroneous(ErroneousTree t, Long find) {
    if (t.getErrorTrees() == null) {
      return null;
    }
    for (Tree e : t.getErrorTrees()) {
      TreePath found = scan(e, find);
      if (found != null) {
        return found;
      }
    }
    return null;
  }

  @Override
  public TreePath reduce(TreePath a, TreePath b) {
    if (a != null) {
      return a;
    }
    return b;
  }
}

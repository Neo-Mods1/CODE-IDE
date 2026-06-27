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

import com.neo.ide.lsp.java.compiler.CompileTask;
import com.neo.ide.lsp.java.utils.FindHelper;
import jdkx.lang.model.element.Name;
import openjdk.source.tree.ClassTree;
import openjdk.source.tree.CompilationUnitTree;
import openjdk.source.tree.IdentifierTree;
import openjdk.source.tree.MemberReferenceTree;
import openjdk.source.tree.MemberSelectTree;
import openjdk.source.tree.MethodTree;
import openjdk.source.tree.NewClassTree;
import openjdk.source.tree.Tree;
import openjdk.source.tree.VariableTree;
import openjdk.source.util.JavacTask;
import openjdk.source.util.SourcePositions;
import openjdk.source.util.TreePath;
import openjdk.source.util.TreePathScanner;
import openjdk.source.util.Trees;

public class FindNameAt extends TreePathScanner<TreePath, Long> {

  private final JavacTask task;
  private CompilationUnitTree root;
  private ClassTree surroundingClass;

  public FindNameAt(CompileTask task) {
    this.task = task.task;
  }

  @Override
  public TreePath reduce(TreePath r1, TreePath r2) {
    if (r1 != null) return r1;
    return r2;
  }

  @Override
  public TreePath visitCompilationUnit(CompilationUnitTree t, Long find) {
    root = t;
    return super.visitCompilationUnit(t, find);
  }

  @Override
  public TreePath visitClass(ClassTree t, Long find) {
    ClassTree push = surroundingClass;
    surroundingClass = t;
    if (contains(t, t.getSimpleName(), find)) {
      surroundingClass = push;
      return getCurrentPath();
    }
    TreePath result = super.visitClass(t, find);
    surroundingClass = push;
    return result;
  }

  @Override
  public TreePath visitMethod(MethodTree t, Long find) {
    Name name = t.getName();
    if (name.contentEquals("<init>")) {
      name = surroundingClass.getSimpleName();
    }
    if (contains(t, name, find)) {
      return getCurrentPath();
    }
    return super.visitMethod(t, find);
  }

  @Override
  public TreePath visitVariable(VariableTree t, Long find) {
    if (contains(t, t.getName(), find)) {
      return getCurrentPath();
    }
    return super.visitVariable(t, find);
  }

  @Override
  public TreePath visitNewClass(NewClassTree t, Long find) {
    long start = Trees.instance(task).getSourcePositions().getStartPosition(root, t);
    long end = start + "new".length();
    if (start <= find && find < end) {
      return getCurrentPath();
    }
    return super.visitNewClass(t, find);
  }

  @Override
  public TreePath visitMemberSelect(MemberSelectTree t, Long find) {
    if (contains(t, t.getIdentifier(), find)) {
      return getCurrentPath();
    }
    return super.visitMemberSelect(t, find);
  }

  @Override
  public TreePath visitMemberReference(MemberReferenceTree t, Long find) {
    if (contains(t, t.getName(), find)) {
      return getCurrentPath();
    }
    return super.visitMemberReference(t, find);
  }

  @Override
  public TreePath visitIdentifier(IdentifierTree t, Long find) {
    if (contains(t, t.getName(), find)) {
      return getCurrentPath();
    }
    return super.visitIdentifier(t, find);
  }

  private boolean contains(Tree t, CharSequence name, long find) {
    SourcePositions pos = Trees.instance(task).getSourcePositions();
    int start = (int) pos.getStartPosition(root, t);
    int end = (int) pos.getEndPosition(root, t);
    if (start == -1 || end == -1) return false;
    start = FindHelper.findNameIn(root, name, start, end);
    end = start + name.length();
    if (start == -1 || end == -1) return false;
    return start <= find && find < end;
  }
}

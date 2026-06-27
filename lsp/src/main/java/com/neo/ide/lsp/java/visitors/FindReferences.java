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

import java.util.List;
import jdkx.lang.model.element.Element;
import openjdk.source.tree.IdentifierTree;
import openjdk.source.tree.MemberReferenceTree;
import openjdk.source.tree.MemberSelectTree;
import openjdk.source.tree.NewClassTree;
import openjdk.source.util.JavacTask;
import openjdk.source.util.TreePath;
import openjdk.source.util.TreePathScanner;
import openjdk.source.util.Trees;

public class FindReferences extends TreePathScanner<Void, List<TreePath>> {

  final JavacTask task;
  final Element find;

  public FindReferences(JavacTask task, Element find) {
    this.task = task;
    this.find = find;
  }

  @Override
  public Void visitNewClass(NewClassTree t, List<TreePath> list) {
    if (check()) {
      list.add(getCurrentPath());
    }
    return super.visitNewClass(t, list);
  }

  @Override
  public Void visitMemberSelect(MemberSelectTree t, List<TreePath> list) {
    if (check()) {
      list.add(getCurrentPath());
    }
    return super.visitMemberSelect(t, list);
  }

  @Override
  public Void visitMemberReference(MemberReferenceTree t, List<TreePath> list) {
    if (check()) {
      list.add(getCurrentPath());
    }
    return super.visitMemberReference(t, list);
  }

  @Override
  public Void visitIdentifier(IdentifierTree t, List<TreePath> list) {
    if (check()) {
      list.add(getCurrentPath());
    }
    return super.visitIdentifier(t, list);
  }

  private boolean check() {
    Element candidate = Trees.instance(task).getElement(getCurrentPath());
    return find.equals(candidate);
  }
}

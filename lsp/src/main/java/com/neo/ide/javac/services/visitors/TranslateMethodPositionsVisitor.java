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



package com.neo.ide.javac.services.visitors;

import openjdk.source.tree.CompilationUnitTree;
import openjdk.source.tree.MethodTree;
import openjdk.source.tree.Tree;
import openjdk.source.tree.VariableTree;
import openjdk.tools.javac.tree.EndPosTable;
import openjdk.tools.javac.tree.JCTree;
import openjdk.tools.javac.tree.JCTree.JCVariableDecl;

/** Helper visitor for partial reparse. Updates tree positions by the given delta. */
public class TranslateMethodPositionsVisitor extends ErrorAwareTreeScanner<Void, Void> {

  private final MethodTree changedMethod;
  private final EndPosTable endPos;
  private final int delta;
  boolean active;
  boolean inMethod;

  public TranslateMethodPositionsVisitor(
      final MethodTree changedMethod, final EndPosTable endPos, final int delta) {
    assert endPos != null;
    this.changedMethod = changedMethod;
    this.endPos = endPos;
    this.delta = delta;

    active = changedMethod == null;
  }

  @Override
  public Void scan(Tree node, Void p) {
    if (active && node != null) {
      if (((JCTree) node).pos >= 0) {
        ((JCTree) node).pos += delta;
      }
    }
    Void result = super.scan(node, p);
    if (inMethod && node != null) {
      endPos.replaceTree((JCTree) node, null);
    }
    if (active && node != null) {
      int pos = endPos.replaceTree((JCTree) node, null);
      int newPos;
      if (pos < 0) {
        newPos = pos;
      } else {
        newPos = pos + delta;
      }
      endPos.storeEnd((JCTree) node, newPos);
    }
    return result;
  }

  @Override
  public Void visitCompilationUnit(CompilationUnitTree node, Void p) {
    return scan(node.getTypeDecls(), p);
  }

  @Override
  public Void visitMethod(MethodTree node, Void p) {
    if (active || inMethod) {
      scan(node.getModifiers(), p);
      scan(node.getReturnType(), p);
      scan(node.getTypeParameters(), p);
      scan(node.getParameters(), p);
      scan(node.getThrows(), p);
    }
    if (node == changedMethod) {
      inMethod = true;
    }
    if (active || inMethod) {
      scan(node.getBody(), p);
    }
    if (inMethod) {
      active = true;
      inMethod = false;
    }
    if (active) {
      scan(node.getDefaultValue(), p);
    }
    return null;
  }

  @Override
  public Void visitVariable(VariableTree node, Void p) {
    JCVariableDecl varDecl = (JCVariableDecl) node;
    if (varDecl.sym != null && active && varDecl.sym.pos >= 0) {
      varDecl.sym.pos += delta;
    }
    return super.visitVariable(node, p);
  }
}

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import openjdk.source.tree.ClassTree;
import openjdk.source.tree.CompilationUnitTree;
import openjdk.source.util.TreeScanner;

public class FindTypeDeclarationNamed extends TreeScanner<ClassTree, String> {
  private List<CharSequence> qualifiedName = new ArrayList<>();

  @Override
  public ClassTree reduce(ClassTree a, ClassTree b) {
    if (a != null) return a;
    return b;
  }

  @Override
  public ClassTree visitCompilationUnit(CompilationUnitTree t, String find) {
    String name = Objects.toString(t.getPackageName(), "");
    qualifiedName.add(name);
    return super.visitCompilationUnit(t, find);
  }

  @Override
  public ClassTree visitClass(ClassTree t, String find) {
    qualifiedName.add(t.getSimpleName());
    if (String.join(".", qualifiedName).equals(find)) {
      return t;
    }
    ClassTree recurse = super.visitClass(t, find);
    qualifiedName.remove(qualifiedName.size() - 1);
    return recurse;
  }
}

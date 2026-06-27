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

import java.io.IOException;
import openjdk.source.tree.CompilationUnitTree;
import openjdk.source.tree.MethodTree;
import openjdk.source.util.JavacTask;
import openjdk.source.util.SourcePositions;
import openjdk.source.util.TreeScanner;
import openjdk.source.util.Trees;

public class PruneMethodBodies extends TreeScanner<StringBuilder, Long> {
  private final JavacTask task;
  private final StringBuilder buf = new StringBuilder();
  private CompilationUnitTree root;

  public PruneMethodBodies(JavacTask task) {
    this.task = task;
  }

  @Override
  public StringBuilder reduce(StringBuilder a, StringBuilder b) {
    return buf;
  }

  @Override
  public StringBuilder visitCompilationUnit(CompilationUnitTree t, Long find) {
    root = t;
    try {
      CharSequence contents = t.getSourceFile().getCharContent(true);
      buf.setLength(0);
      buf.append(contents);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    super.visitCompilationUnit(t, find);
    return buf;
  }

  @Override
  public StringBuilder visitMethod(MethodTree t, Long find) {
    SourcePositions pos = Trees.instance(task).getSourcePositions();
    if (t.getBody() == null) {
      return buf;
    }
    long start = pos.getStartPosition(root, t.getBody());
    long end = pos.getEndPosition(root, t.getBody());
    if (!(start <= find && find < end)) {
      for (int i = (int) start + 1; i < end - 1; i++) {
        if (!Character.isWhitespace(buf.charAt(i))) {
          buf.setCharAt(i, ' ');
        }
      }
      return buf;
    }
    super.visitMethod(t, find);
    return buf;
  }
}

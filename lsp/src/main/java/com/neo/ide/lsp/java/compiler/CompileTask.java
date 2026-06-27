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



package com.neo.ide.lsp.java.compiler;

import androidx.annotation.NonNull;
import com.neo.ide.javac.services.partial.DiagnosticListenerImpl;
import java.nio.file.Path;
import java.util.List;
import jdkx.tools.Diagnostic;
import jdkx.tools.JavaFileObject;
import openjdk.source.tree.CompilationUnitTree;
import openjdk.tools.javac.api.JavacTaskImpl;

public class CompileTask implements AutoCloseable {

  public final JavacTaskImpl task;
  public final List<CompilationUnitTree> roots;
  public final List<Diagnostic<? extends JavaFileObject>> diagnostics;
  public final CompileBatch compileBatch;
  public final DiagnosticListenerImpl diagnosticListener;

  public CompileTask(
      @NonNull CompileBatch compileBatch, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
    this.compileBatch = compileBatch;
    this.task = compileBatch.task;
    this.roots = compileBatch.roots;
    this.diagnostics = diagnostics;
    this.diagnosticListener = compileBatch.diagnosticListener;
  }

  public CompilationUnitTree root() {
    if (roots.size() != 1) {
      throw new RuntimeException("No compilation units found. Roots: " + roots.size());
    }
    return roots.get(0);
  }

  public CompilationUnitTree root(Path file) {
    for (CompilationUnitTree root : roots) {
      if (root.getSourceFile().toUri().equals(file.toUri())) {
        return root;
      }
    }
    throw new RuntimeException("Compilation unit not found");
  }

  public CompilationUnitTree root(JavaFileObject file) {
    for (CompilationUnitTree root : roots) {
      if (root.getSourceFile().toUri().equals(file.toUri())) {
        return root;
      }
    }
    throw new RuntimeException("Compilation unit not found");
  }

  @Override
  public void close() {}
}

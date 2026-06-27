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



package com.neo.ide.lsp.java.providers;

import androidx.annotation.NonNull;
import com.neo.ide.lsp.java.compiler.CompilerProvider;
import com.neo.ide.lsp.java.visitors.FindBiggerRange;
import com.neo.ide.lsp.models.ExpandSelectionParams;
import com.neo.ide.models.Range;
import openjdk.source.tree.CompilationUnitTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Selection provider implementation for Java.
 *
 * @author Akash Yadav
 */
public class JavaSelectionProvider {

  private static final Logger LOG = LoggerFactory.getLogger(JavaSelectionProvider.class);
  private final CompilerProvider compiler;

  public JavaSelectionProvider(CompilerProvider compiler) {
    this.compiler = compiler;
  }

  @NonNull
  public Range expandSelection(@NonNull ExpandSelectionParams params) {
    return compiler
        .compile(params.getFile())
        .get(
            task -> {
              final CompilationUnitTree root = task.root(params.getFile());
              final FindBiggerRange rangeFinder = new FindBiggerRange(task.task, root);
              final Range range = rangeFinder.scan(root, params.getSelection());

              if (range != null) {
                LOG.info("Expanding selection to range: {}", range);
                return range;
              }

              LOG.debug("Unable to expand selection");
              return params.getSelection();
            });
  }
}

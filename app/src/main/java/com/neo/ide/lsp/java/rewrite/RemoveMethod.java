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


package com.neo.ide.lsp.java.rewrite;

import androidx.annotation.NonNull;
import com.neo.ide.lsp.java.compiler.CompilerProvider;
import com.neo.ide.lsp.java.utils.EditHelper;
import com.neo.ide.lsp.java.utils.FindHelper;
import com.neo.ide.lsp.models.TextEdit;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import openjdk.source.util.Trees;

public class RemoveMethod extends Rewrite {

  final String className, methodName;
  final String[] erasedParameterTypes;

  public RemoveMethod(String className, String methodName, String[] erasedParameterTypes) {
    this.className = className;
    this.methodName = methodName;
    this.erasedParameterTypes = erasedParameterTypes;
  }

  @NonNull
  @Override
  public Map<Path, TextEdit[]> rewrite(CompilerProvider compiler) {
    Path file = compiler.findTypeDeclaration(className);
    if (file == CompilerProvider.NOT_FOUND) {
      return CANCELLED;
    }

    return compiler
        .compile(file)
        .get(
            task -> {
              final var methodElement =
                  FindHelper.findMethod(task, className, methodName, erasedParameterTypes);
              if (methodElement == null) {
                return CANCELLED;
              }

              final var methodTree = Trees.instance(task.task).getTree(methodElement);
              if (methodTree == null) {
                return CANCELLED;
              }

              TextEdit[] edits = {EditHelper.removeTree(task.task, task.root(), methodTree)};
              return Collections.singletonMap(file, edits);
            });
  }
}

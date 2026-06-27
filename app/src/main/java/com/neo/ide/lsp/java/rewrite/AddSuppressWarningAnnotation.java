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
import com.neo.ide.lsp.java.compiler.SynchronizedTask;
import com.neo.ide.lsp.java.utils.FindHelper;
import com.neo.ide.lsp.models.TextEdit;
import com.neo.ide.models.Position;
import com.neo.ide.models.Range;
import com.neo.ide.preferences.utils.EditorUtilKt;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import openjdk.source.util.Trees;

public class AddSuppressWarningAnnotation extends Rewrite {

  final String className, methodName;
  final String[] erasedParameterTypes;

  public AddSuppressWarningAnnotation(
      String className, String methodName, String[] erasedParameterTypes) {
    this.className = className;
    this.methodName = methodName;
    this.erasedParameterTypes = erasedParameterTypes;
  }

  @NonNull
  @Override
  public Map<Path, TextEdit[]> rewrite(@NonNull CompilerProvider compiler) {
    Path file = compiler.findTypeDeclaration(className);
    if (file == CompilerProvider.NOT_FOUND) {
      return CANCELLED;
    }
    SynchronizedTask synchronizedTask = compiler.compile(file);
    return synchronizedTask.get(
        task -> {
          final var trees = Trees.instance(task.task);
          final var methodElement =
              FindHelper.findMethod(task, className, methodName, erasedParameterTypes);
          if (methodElement == null) {
            return CANCELLED;
          }
          final var methodTree = trees.getTree(methodElement);
          if (methodTree == null) {
            return CANCELLED;
          }
          final var startMethod = (int) trees.getSourcePositions()
              .getStartPosition(task.root(), methodTree);
          final var lines = task.root().getLineMap();
          final var line = (int) lines.getLineNumber(startMethod);
          final var column = (int) lines.getColumnNumber(startMethod);
          final var startLine = (int) lines.getStartPosition(line);
          final var indent = EditorUtilKt.indentationString(startMethod - startLine);
          final var insertText = "@SuppressWarnings(\"unchecked\")\n" + indent;
          final var insertPoint = new Position(line - 1, column - 1);
          final var edits = new TextEdit[]{
              new TextEdit(new Range(insertPoint, insertPoint), insertText)};
          return Collections.singletonMap(file, edits);
        });
  }
}

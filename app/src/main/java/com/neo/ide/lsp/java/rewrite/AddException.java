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
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import jdkx.lang.model.element.ExecutableElement;
import openjdk.source.util.Trees;

public class AddException extends Rewrite {

  final String className, methodName;
  final String[] erasedParameterTypes;
  final String exceptionType;

  public AddException(String className, String methodName, String[] erasedParameterTypes,
      String exceptionType
  ) {
    this.className = className;
    this.methodName = methodName;
    this.erasedParameterTypes = erasedParameterTypes;
    this.exceptionType = exceptionType;
  }

  @NonNull
  @Override
  public Map<Path, TextEdit[]> rewrite(@NonNull CompilerProvider compiler) {
    Path file = compiler.findTypeDeclaration(className);
    if (file == CompilerProvider.NOT_FOUND) {
      return CANCELLED;
    }

    SynchronizedTask synchronizedTask = compiler.compile(file);
    return synchronizedTask.get(task -> {
      Trees trees = Trees.instance(task.task);
      final var type = task.task.getElements().getTypeElement(className);
      if (type == null) {
        return CANCELLED;
      }

      ExecutableElement methodElement = FindHelper.findMethod(task, className, methodName,
          erasedParameterTypes);
      if (methodElement == null) {
        return CANCELLED;
      }

      final var methodTree = trees.getTree(methodElement);
      if (methodTree == null || methodTree.getBody() == null) {
        return CANCELLED;
      }

      final var pos = trees.getSourcePositions();
      final var lines = task.root().getLineMap();

      final var index = pos.getStartPosition(task.root(), methodTree.getBody());
      int line = (int) lines.getLineNumber(index);
      int column = (int) lines.getColumnNumber(index);
      Position insertPos = new Position(line - 1, column - 1);
      String simpleName = exceptionType;
      int lastDot = simpleName.lastIndexOf('.');
      if (lastDot != -1) {
        simpleName = exceptionType.substring(lastDot + 1);
      }

      String insertText;
      if (methodTree.getThrows().isEmpty()) {
        insertText = "throws " + simpleName + " ";
      } else {
        insertText = ", " + simpleName + " ";
      }

      TextEdit insertThrows = new TextEdit(new Range(insertPos, insertPos), insertText);
      // TODO add import if needed
      TextEdit[] edits = {insertThrows};
      return Collections.singletonMap(file, edits);
    });
  }
}

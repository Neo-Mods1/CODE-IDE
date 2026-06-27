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
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jdkx.lang.model.element.TypeElement;
import jdkx.lang.model.type.DeclaredType;
import openjdk.source.tree.CompilationUnitTree;
import openjdk.source.tree.ExpressionTree;
import openjdk.source.tree.LineMap;
import openjdk.source.tree.MethodTree;
import openjdk.source.util.JavacTask;
import openjdk.source.util.SourcePositions;
import openjdk.source.util.TreePath;
import openjdk.source.util.Trees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveException extends Rewrite {

  private static final Pattern THROWS = Pattern.compile("\\s*\\bthrows\\b");
  final String className, methodName;
  final String[] erasedParameterTypes;
  final String exceptionType;

  private static final Logger LOG = LoggerFactory.getLogger(RemoveException.class);

  public RemoveException(
      String className, String methodName, String[] erasedParameterTypes, String exceptionType) {
    this.className = className;
    this.methodName = methodName;
    this.erasedParameterTypes = erasedParameterTypes;
    this.exceptionType = exceptionType;
  }

  @NonNull
  @Override
  public Map<Path, TextEdit[]> rewrite(CompilerProvider compiler) {
    Path file = compiler.findTypeDeclaration(className);

    if (file == CompilerProvider.NOT_FOUND) {
      LOG.warn("Unable to find source file for class: {}", this.className);
      return CANCELLED;
    }

    SynchronizedTask synchronizedTask = compiler.compile(file);
    return synchronizedTask.get(
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

          if (methodTree.getThrows().size() == 1) {
            TextEdit delete = removeEntireThrows(task.task, task.root(), methodTree);
            if (delete == TextEdit.NONE) {
              return CANCELLED;
            }

            TextEdit[] edits = {delete};
            return Collections.singletonMap(file, edits);
          }
          TextEdit[] edits = {removeSingleException(task.task, task.root(), methodTree)};
          return Collections.singletonMap(file, edits);
        });
  }

  private TextEdit removeEntireThrows(JavacTask task, CompilationUnitTree root, MethodTree method) {
    Trees trees = Trees.instance(task);
    SourcePositions pos = trees.getSourcePositions();
    int startMethod = (int) pos.getStartPosition(root, method);
    CharSequence contents;
    try {
      contents = root.getSourceFile().getCharContent(true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Matcher matcher = THROWS.matcher(contents);
    if (!matcher.find(startMethod)) {
      return TextEdit.NONE;
    }

    LineMap lines = root.getLineMap();
    int start = matcher.start();
    int startLine = (int) lines.getLineNumber(start);
    int startColumn = (int) lines.getColumnNumber(start);
    Position startPos = new Position(startLine - 1, startColumn - 1);
    ExpressionTree lastException = method.getThrows().get(method.getThrows().size() - 1);
    int end = (int) pos.getEndPosition(root, lastException);
    int endLine = (int) lines.getLineNumber(end);
    int endColumn = (int) lines.getColumnNumber(end);
    Position endPos = new Position(endLine - 1, endColumn - 1);
    return new TextEdit(new Range(startPos, endPos), "");
  }

  private TextEdit removeSingleException(
      JavacTask task, CompilationUnitTree root, MethodTree method) {
    int i = findNamedException(task, root, method);
    if (i == -1) {
      return TextEdit.NONE;
    }

    Trees trees = Trees.instance(task);
    SourcePositions pos = trees.getSourcePositions();
    ExpressionTree exn = method.getThrows().get(i);
    long start = pos.getStartPosition(root, exn);
    long end = pos.getEndPosition(root, exn);
    if (i == 0) {
      end = removeTrailingComma(root, end);
    } else {
      start = removeLeadingComma(root, start);
    }

    LineMap lines = root.getLineMap();
    int startLine = (int) lines.getLineNumber(start);
    int startColumn = (int) lines.getColumnNumber(start);
    Position startPos = new Position(startLine - 1, startColumn - 1);
    int endLine = (int) lines.getLineNumber(end);
    int endColumn = (int) lines.getColumnNumber(end);
    Position endPos = new Position(endLine - 1, endColumn - 1);
    return new TextEdit(new Range(startPos, endPos), "");
  }

  private int findNamedException(JavacTask task, CompilationUnitTree root, MethodTree method) {
    Trees trees = Trees.instance(task);
    for (int i = 0; i < method.getThrows().size(); i++) {
      ExpressionTree e = method.getThrows().get(i);
      TreePath path = trees.getPath(root, e);
      DeclaredType type = (DeclaredType) trees.getTypeMirror(path);
      TypeElement el = (TypeElement) type.asElement();
      if (el.getQualifiedName().contentEquals(exceptionType)) {
        return i;
      }
    }
    return -1;
  }

  private int removeLeadingComma(CompilationUnitTree root, long start) {
    CharSequence contents = contents(root);
    for (int i = (int) start; i > 0; i--) {
      if (contents.charAt(i) == ',') {
        return i;
      }
    }
    return -1;
  }

  private int removeTrailingComma(CompilationUnitTree root, long end) {
    CharSequence contents = contents(root);
    for (int i = (int) end; i < contents.length(); i++) {
      if (contents.charAt(i) == ',') {
        if (contents.charAt(i + 1) == ' ') {
          return i + 2;
        } else {
          return i + 1;
        }
      }
    }
    return -1;
  }

  private CharSequence contents(CompilationUnitTree root) {
    try {
      return root.getSourceFile().getCharContent(true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}

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

import static com.neo.ide.lsp.java.rewrite.ConvertVariableToStatement.findVariable;
import static com.neo.ide.lsp.java.rewrite.ConvertVariableToStatement.isExpressionStatement;

import androidx.annotation.NonNull;
import com.neo.ide.lsp.java.compiler.CompilerProvider;
import com.neo.ide.lsp.java.parser.ParseTask;
import com.neo.ide.lsp.models.TextEdit;
import com.neo.ide.models.Position;
import com.neo.ide.models.Range;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import jdkx.lang.model.element.Modifier;
import openjdk.source.tree.ExpressionTree;
import openjdk.source.tree.LineMap;
import openjdk.source.tree.VariableTree;
import openjdk.source.util.SourcePositions;
import openjdk.source.util.Trees;

public class ConvertFieldToBlock extends Rewrite {
  final Path file;
  final int position;

  public ConvertFieldToBlock(Path file, int position) {
    this.file = file;
    this.position = position;
  }

  @NonNull
  @Override
  public Map<Path, TextEdit[]> rewrite(@NonNull CompilerProvider compiler) {
    ParseTask task = compiler.parse(file);
    Trees trees = Trees.instance(task.task);
    SourcePositions pos = trees.getSourcePositions();
    LineMap lines = task.root.getLineMap();
    VariableTree variable = findVariable(task, position);
    if (variable == null) {
      return CANCELLED;
    }
    ExpressionTree expression = variable.getInitializer();
    if (!isExpressionStatement(expression)) {
      return CANCELLED;
    }
    long start = pos.getStartPosition(task.root, variable);
    long end = pos.getStartPosition(task.root, expression);
    int startLine = (int) lines.getLineNumber(start);
    int startColumn = (int) lines.getColumnNumber(start);
    Position startPos = new Position(startLine - 1, startColumn - 1);
    int endLine = (int) lines.getLineNumber(end);
    int endColumn = (int) lines.getColumnNumber(end);
    Position endPos = new Position(endLine - 1, endColumn - 1);
    Range deleteLhs = new Range(startPos, endPos);
    TextEdit fixLhs = new TextEdit(deleteLhs, "{ ");
    if (variable.getModifiers().getFlags().contains(Modifier.STATIC)) {
      fixLhs.setNewText("static { ");
    }
    long right = pos.getEndPosition(task.root, variable);
    int rightLine = (int) lines.getLineNumber(right);
    int rightColumn = (int) lines.getColumnNumber(right);
    Position rightPos = new Position(rightLine - 1, rightColumn - 1);
    Range insertRight = new Range(rightPos, rightPos);
    TextEdit fixRhs = new TextEdit(insertRight, " }");
    TextEdit[] edits = {fixLhs, fixRhs};
    return Collections.singletonMap(file, edits);
  }
}

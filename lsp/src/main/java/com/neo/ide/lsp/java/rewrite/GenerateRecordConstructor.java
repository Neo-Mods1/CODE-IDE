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
import com.neo.ide.lsp.java.compiler.CompileTask;
import com.neo.ide.lsp.java.compiler.CompilerProvider;
import com.neo.ide.lsp.java.compiler.SynchronizedTask;
import com.neo.ide.lsp.java.utils.EditHelper;
import com.neo.ide.lsp.models.TextEdit;
import com.neo.ide.models.Position;
import com.neo.ide.models.Range;
import com.neo.ide.preferences.internal.EditorPreferences;
import com.neo.ide.preferences.utils.EditorUtilKt;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import jdkx.lang.model.element.Modifier;
import jdkx.lang.model.element.TypeElement;
import openjdk.source.tree.ClassTree;
import openjdk.source.tree.MethodTree;
import openjdk.source.tree.Tree;
import openjdk.source.tree.VariableTree;
import openjdk.source.util.SourcePositions;
import openjdk.source.util.Trees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenerateRecordConstructor extends Rewrite {

  private static final Logger LOG = LoggerFactory.getLogger(GenerateRecordConstructor.class);
  final String className;

  public GenerateRecordConstructor(String className) {
    this.className = className;
  }

  @NonNull
  @Override
  public Map<Path, TextEdit[]> rewrite(@NonNull CompilerProvider compiler) {
    LOG.info("Generate default constructor for {}...", className);
    // TODO this needs to fall back on looking for inner classes and package-private classes
    Path file = compiler.findTypeDeclaration(className);

    if (file == CompilerProvider.NOT_FOUND) {
      LOG.warn("Unable to find source file for class: {}", this.className);
      return CANCELLED;
    }

    SynchronizedTask synchronizedTask = compiler.compile(file);
    return synchronizedTask.get(
        task -> {
          TypeElement typeElement = task.task.getElements().getTypeElement(className);
          ClassTree typeTree = Trees.instance(task.task).getTree(typeElement);
          List<VariableTree> fields = fieldsNeedingInitialization(typeTree);
          String parameters = generateParameters(task, fields);
          String initializers = generateInitializers(fields);
          StringBuilder buf = new StringBuilder();
          buf.append("\n");
          if (typeTree.getModifiers().getFlags().contains(Modifier.PUBLIC)) {
            buf.append("public ");
          }

          buf.append(simpleName(className))
              .append("(")
              .append(parameters)
              .append(") {\n    ")
              .append(initializers)
              .append("\n}");
          String string = buf.toString();
          int indent = EditHelper.indent(task.task, task.root(), typeTree)
              + EditorPreferences.INSTANCE.getTabSize();
          string = string.replaceAll("\n", "\n" + EditorUtilKt.indentationString(indent));
          string = string + "\n\n";
          Position insert = insertPoint(task, typeTree);
          TextEdit[] edits = {new TextEdit(new Range(insert, insert), string)};
          return Collections.singletonMap(file, edits);
        });
  }

  private List<VariableTree> fieldsNeedingInitialization(ClassTree typeTree) {
    List<VariableTree> fields = new ArrayList<>();
    for (Tree member : typeTree.getMembers()) {
      if (!(member instanceof VariableTree)) {
        continue;
      }
      VariableTree field = (VariableTree) member;
      if (field.getInitializer() != null) {
        continue;
      }
      Set<Modifier> flags = field.getModifiers().getFlags();
      if (flags.contains(Modifier.STATIC)) {
        continue;
      }
      if (!flags.contains(Modifier.FINAL)) {
        continue;
      }
      fields.add(field);
    }

    return fields;
  }

  private String generateParameters(CompileTask task, List<VariableTree> fields) {
    StringJoiner join = new StringJoiner(", ");
    for (VariableTree f : fields) {
      join.add(extract(task, f.getType()) + " " + f.getName());
    }
    return join.toString();
  }

  private CharSequence extract(CompileTask task, Tree typeTree) {
    try {
      CharSequence contents = task.root().getSourceFile().getCharContent(true);
      SourcePositions pos = Trees.instance(task.task).getSourcePositions();
      int start = (int) pos.getStartPosition(task.root(), typeTree);
      int end = (int) pos.getEndPosition(task.root(), typeTree);
      return contents.subSequence(start, end);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String generateInitializers(List<VariableTree> fields) {
    StringJoiner join = new StringJoiner("\n    ");
    for (VariableTree f : fields) {
      join.add("this." + f.getName() + " = " + f.getName() + ";");
    }
    return join.toString();
  }

  private String simpleName(String className) {
    int dot = className.lastIndexOf('.');
    if (dot != -1) {
      return className.substring(dot + 1);
    }
    return className;
  }

  private Position insertPoint(CompileTask task, ClassTree typeTree) {
    for (Tree member : typeTree.getMembers()) {
      if (member.getKind() == Tree.Kind.METHOD) {
        MethodTree method = (MethodTree) member;
        if (method.getReturnType() == null) {
          continue;
        }
        LOG.info("...insert constructor before {}", method.getName());
        return EditHelper.insertBefore(task.task, task.root(), method);
      }
    }
    LOG.info("...insert constructor at end of class");
    return EditHelper.insertAtEndOfClass(task.task, task.root(), typeTree);
  }
}

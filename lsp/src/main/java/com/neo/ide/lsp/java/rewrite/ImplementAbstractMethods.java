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
import androidx.annotation.Nullable;
import com.neo.ide.lsp.java.compiler.CompileTask;
import com.neo.ide.lsp.java.compiler.CompilerProvider;
import com.neo.ide.lsp.java.compiler.SynchronizedTask;
import com.neo.ide.lsp.java.utils.EditHelper;
import com.neo.ide.lsp.java.utils.JavaPoetUtils;
import com.neo.ide.lsp.java.visitors.FindAnonymousTypeDeclaration;
import com.neo.ide.lsp.java.visitors.FindTypeDeclarationAt;
import com.neo.ide.lsp.models.CodeActionItem;
import com.neo.ide.lsp.models.Command;
import com.neo.ide.lsp.models.TextEdit;
import com.neo.ide.models.Position;
import com.neo.ide.models.Range;
import com.neo.ide.preferences.internal.EditorPreferences;
import com.neo.ide.preferences.utils.EditorUtilKt;
import com.squareup.javapoet.MethodSpec;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.stream.Collectors;
import jdkx.lang.model.element.Element;
import jdkx.lang.model.element.ElementKind;
import jdkx.lang.model.element.ExecutableElement;
import jdkx.lang.model.element.Modifier;
import jdkx.lang.model.element.TypeElement;
import jdkx.lang.model.util.Elements;
import openjdk.source.tree.ClassTree;
import openjdk.source.tree.CompilationUnitTree;
import openjdk.source.tree.ImportTree;
import openjdk.source.tree.Tree;
import openjdk.source.util.Trees;
import openjdk.tools.javac.util.JCDiagnostic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImplementAbstractMethods extends Rewrite {

  private static final Logger LOG = LoggerFactory.getLogger(ImplementAbstractMethods.class);
  private final String className;
  private final String classFile;
  private final long position;

  public ImplementAbstractMethods(@NonNull JCDiagnostic diagnostic) {
    Object[] args = diagnostic.getArgs();
    String className = args[0].toString();

    if (!className.contains("<anonymous")) {
      this.className = className;
      this.classFile = className;
      this.position = 0;
    } else {
      className = className.substring("<anonymous ".length(), className.length() - 1);
      className = className.substring(0, className.indexOf('$'));
      this.classFile = className;
      this.className = args[2].toString();
      this.position = diagnostic.getStartPosition();
    }
  }

  @NonNull
  @Override
  public Map<Path, TextEdit[]> rewrite(@NonNull CompilerProvider compiler) {
    final Path file = compiler.findTypeDeclaration(this.classFile);
    if (file == CompilerProvider.NOT_FOUND) {
      LOG.warn("Unable to find source file for class: {} classFile={}", this.className,
          this.classFile);
      return CANCELLED;
    }

    final SynchronizedTask synchronizedTask = compiler.compile(file);
    return synchronizedTask.get(
        task -> {
          StringJoiner insertText = new StringJoiner("\n");
          Elements elements = task.task.getElements();
          Trees trees = Trees.instance(task.task);
          TypeElement thisClass = elements.getTypeElement(this.className);

          ClassTree thisTree = getClassTree(task, file);
          if (thisTree == null) {
            thisTree = trees.getTree(thisClass);
          }

          final Set<String> imports = new TreeSet<>();
          int indent = EditHelper.indent(task.task, task.root(), thisTree)
              + EditorPreferences.INSTANCE.getTabSize();
          for (Element member : elements.getAllMembers(thisClass)) {
            if (member.getKind() == ElementKind.METHOD
                && member.getModifiers().contains(Modifier.ABSTRACT)) {
              ExecutableElement method = (ExecutableElement) member;
              final MethodSpec methodSpec = MethodSpec.overriding(method).build();
              String text = "\n" + JavaPoetUtils.print(methodSpec, imports, false);
              text = text.replaceAll("\n", "\n" + EditorUtilKt.indentationString(indent));
              insertText.add(text);
            }
          }

          Position insert = EditHelper.insertAtEndOfClass(task.task, task.root(), thisTree);
          final List<TextEdit> edits = new ArrayList<>();
          edits.add(new TextEdit(new Range(insert, insert), insertText + "\n"));
          addImports(compiler, task, file, imports, edits);

          return Collections.singletonMap(file, edits.toArray(new TextEdit[0]));
        });
  }

  private void addImports(
      CompilerProvider compiler,
      CompileTask task,
      Path file,
      Set<String> imports,
      List<TextEdit> edits) {
    imports =
        imports.stream().filter(name -> !name.startsWith("java.lang.")).collect(Collectors.toSet());
    for (String name : imports) {
      final List<TextEdit> importEdits =
          EditHelper.addImportIfNeeded(compiler, file, getFileImports(task, file), name);
      if (importEdits != null && !importEdits.isEmpty()) {
        edits.addAll(importEdits);
      }
    }
  }

  private Set<String> getFileImports(@NonNull CompileTask task, Path file) {
    return task.root(file).getImports().stream()
        .map(ImportTree::getQualifiedIdentifier)
        .map(Tree::toString)
        .collect(Collectors.toSet());
  }

  @Nullable
  private ClassTree getClassTree(@NonNull CompileTask task, Path file) {
    ClassTree thisTree = null;
    CompilationUnitTree root = task.root(file);
    if (root == null) {
      return null;
    }

    if (position != 0) {
      final FindTypeDeclarationAt scanner = new FindTypeDeclarationAt(task.task);
      thisTree = scanner.scan(root, position);
    }

    if (thisTree == null) {
      final FindAnonymousTypeDeclaration scanner =
          new FindAnonymousTypeDeclaration(task.task, root);
      thisTree = scanner.scan(root, position);
    }

    return thisTree;
  }

  @Override
  protected void finalizeCodeAction(@NonNull CodeActionItem action) {
    action.setCommand(new Command("Format code", Command.FORMAT_CODE));
  }
}

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
import com.neo.ide.lsp.java.compiler.CompileTask;
import com.neo.ide.lsp.java.compiler.CompilerProvider;
import com.neo.ide.lsp.java.compiler.SynchronizedTask;
import com.neo.ide.lsp.java.utils.CancelChecker;
import com.neo.ide.lsp.java.utils.FindHelper;
import com.neo.ide.lsp.java.utils.NavigationHelper;
import com.neo.ide.lsp.java.visitors.FindReferences;
import com.neo.ide.lsp.models.ReferenceParams;
import com.neo.ide.lsp.models.ReferenceResult;
import com.neo.ide.models.Location;
import com.neo.ide.progress.ICancelChecker;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import jdkx.lang.model.element.Element;
import jdkx.lang.model.element.TypeElement;
import openjdk.source.tree.CompilationUnitTree;
import openjdk.source.util.TreePath;

public class ReferenceProvider extends CancelableServiceProvider {

  public static final List<Location> NOT_SUPPORTED = Collections.emptyList();
  private final CompilerProvider compiler;
  private Path file;
  private int line, column;

  public ReferenceProvider(CompilerProvider compiler, ICancelChecker checker) {
    super(checker);
    this.compiler = compiler;
  }

  @NonNull
  public ReferenceResult findReferences(@NonNull ReferenceParams params) {
    this.file = params.getFile();

    // 1-based line and column indexes
    this.line = params.getPosition().getLine() + 1;
    this.column = params.getPosition().getColumn() + 1;

    List<Location> locations;
    try {
      locations = find();
    } catch (Exception err) {
      if (!CancelChecker.isCancelled(err)) {
        throw err;
      }
      locations = new ArrayList<>();
    }

    return new ReferenceResult(locations);
  }

  public List<Location> find() {
    abortIfCancelled();
    final SynchronizedTask synchronizedTask = compiler.compile(file);

    // findTypeReferences and findMemberReferences initiate another compilation task
    // However, initiating a compilation task while another compilation is in progress will result in a deadlock
    // Therefore, we return a supplier from the current synchronized task and
    final Supplier<List<Location>> result = synchronizedTask.get(
        task -> {
          abortIfCancelled();
          Element element = NavigationHelper.findElement(task, file, line, column, this);
          if (element == null) {
            return () -> NOT_SUPPORTED;
          }

          if (NavigationHelper.isLocal(element)) {
            // findReferences method here uses the compilation task object
            // however, finding the references lazily using supplier will leak this task
            final var references = findReferences(task);
            return () -> references;
          }

          if (NavigationHelper.isType(element)) {
            TypeElement type = (TypeElement) element;
            String className = type.getQualifiedName().toString();
            return () -> findTypeReferences(className);
          }

          if (NavigationHelper.isMember(element)) {
            final var parentClass = (TypeElement) element.getEnclosingElement();
            final var className = parentClass.getQualifiedName().toString();

            var memberName = element.getSimpleName().toString();
            if (memberName.equals("<init>")) {
              memberName = parentClass.getSimpleName().toString();
            }

            String finalMemberName = memberName;
            return () -> findMemberReferences(className, finalMemberName);
          }

          return () -> NOT_SUPPORTED;
        });

    return result.get();
  }

  private List<Location> findTypeReferences(String className) {
    abortIfCancelled();
    Path[] files = compiler.findTypeReferences(className);
    if (files.length == 0) {
      return Collections.emptyList();
    }

    abortIfCancelled();
    return compiler.compile(files).get(this::findReferences);
  }

  private List<Location> findMemberReferences(String className, String memberName) {
    abortIfCancelled();
    final var files = compiler.findMemberReferences(className, memberName);
    if (files.length == 0) {
      return Collections.emptyList();
    }

    abortIfCancelled();
    return compiler.compile(files).get(this::findReferences);
  }

  private List<Location> findReferences(CompileTask task) {
    abortIfCancelled();
    Element element = NavigationHelper.findElement(task, file, line, column, this);
    List<TreePath> paths = new ArrayList<>();
    for (CompilationUnitTree root : task.roots) {
      abortIfCancelled();
      new FindReferences(task.task, element).scan(root, paths);
    }
    List<Location> locations = new ArrayList<>();
    for (TreePath p : paths) {
      abortIfCancelled();
      locations.add(FindHelper.location(task, p));
    }
    return locations;
  }
}
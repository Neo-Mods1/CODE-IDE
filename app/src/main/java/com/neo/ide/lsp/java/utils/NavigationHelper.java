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





package com.neo.ide.lsp.java.utils;

import androidx.annotation.Nullable;
import com.neo.ide.lsp.java.compiler.CompileTask;
import com.neo.ide.lsp.java.visitors.FindNameAt;
import com.neo.ide.progress.ICancelChecker;
import java.nio.file.Path;
import jdkx.lang.model.element.Element;
import jdkx.lang.model.element.Modifier;
import openjdk.source.tree.CompilationUnitTree;
import openjdk.source.util.TreePath;
import openjdk.source.util.Trees;

public class NavigationHelper {

  @Nullable
  public static Element findElement(CompileTask task, Path file, int line, int column, ICancelChecker cancelChecker) {
    Trees trees = Trees.instance(task.task);
    for (CompilationUnitTree root : task.roots) {
      if (cancelChecker != null) {
        cancelChecker.abortIfCancelled();
      }

      if (root.getSourceFile().toUri().equals(file.toUri())) {
        long cursor = root.getLineMap().getPosition(line, column);
        TreePath path = new FindNameAt(task).scan(root, cursor);
        if (cancelChecker != null) {
          cancelChecker.abortIfCancelled();
        }
        if (path == null) {
          return null;
        }
        return trees.getElement(path);
      }
    }
    throw new RuntimeException("file not found");
  }

  public static boolean isLocal(Element element) {
    if (element.getModifiers().contains(Modifier.PRIVATE)) {
      return true;
    }
    switch (element.getKind()) {
      case EXCEPTION_PARAMETER:
      case LOCAL_VARIABLE:
      case PARAMETER:
      case TYPE_PARAMETER:
        return true;
      default:
        return false;
    }
  }

  public static boolean isMember(Element element) {
    switch (element.getKind()) {
      case ENUM_CONSTANT:
      case FIELD:
      case METHOD:
      case CONSTRUCTOR:
        return true;
      default:
        return false;
    }
  }

  public static boolean isType(Element element) {
    switch (element.getKind()) {
      case ANNOTATION_TYPE:
      case CLASS:
      case ENUM:
      case INTERFACE:
        return true;
      default:
        return false;
    }
  }
}

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

import com.neo.ide.lsp.java.compiler.CompileTask;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import jdkx.lang.model.element.Element;
import jdkx.lang.model.element.Modifier;
import jdkx.lang.model.element.TypeElement;
import jdkx.lang.model.type.DeclaredType;
import jdkx.lang.model.util.Elements;
import openjdk.source.tree.Scope;
import openjdk.source.util.Trees;

public class ScopeHelper {
  public static List<Element> scopeMembers(
      CompileTask task, Scope inner, Predicate<CharSequence> filter) {
    Trees trees = Trees.instance(task.task);
    Elements elements = task.task.getElements();
    boolean isStatic = false;
    List<Element> list = new ArrayList<>();
    for (Scope scope : fastScopes(inner)) {
      if (scope.getEnclosingMethod() != null) {
        isStatic = isStatic || scope.getEnclosingMethod().getModifiers().contains(Modifier.STATIC);
      }
      for (Element member : scope.getLocalElements()) {
        if (!filter.test(member.getSimpleName())) {
          continue;
        }
        if ("<init>".contentEquals(member.getSimpleName())
            || "<clinit>".contentEquals(member.getSimpleName())) {
          continue;
        }
        if (isStatic && member.getSimpleName().contentEquals("this")) {
          continue;
        }
        if (isStatic && member.getSimpleName().contentEquals("super")) {
          continue;
        }
        list.add(member);
      }
      if (scope.getEnclosingClass() != null) {
        TypeElement typeElement = scope.getEnclosingClass();
        DeclaredType typeType = (DeclaredType) typeElement.asType();
        for (Element member : elements.getAllMembers(typeElement)) {
          if (!filter.test(member.getSimpleName())) {
            continue;
          }
          if (!trees.isAccessible(scope, member, typeType)) {
            continue;
          }
          if (isStatic && !member.getModifiers().contains(Modifier.STATIC)) {
            continue;
          }
          list.add(member);
        }
        isStatic = isStatic || typeElement.getModifiers().contains(Modifier.STATIC);
      }
    }
    return list;
  }

  // TODO is this still necessary? Test speed. We could get rid of the extra static-imports step.
  public static List<Scope> fastScopes(Scope start) {
    List<Scope> scopes = new ArrayList<>();
    for (Scope s = start; s != null; s = s.getEnclosingScope()) {
      scopes.add(s);
    }
    // Scopes may be contained in an enclosing scope.
    // The outermost scope contains those elements available via "star import" declarations;
    // the scope within that contains the top level elements of the compilation unit, including
    // any named imports.
    // https://parent.docs.oracle.com/en/java/javase/11/docs/api/jdk.compiler/com/sun/source/tree/Scope.html
    return scopes.subList(0, scopes.size() - 2);
  }
}

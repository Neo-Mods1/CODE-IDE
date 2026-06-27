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

import java.util.stream.Collectors;
import jdkx.lang.model.type.ArrayType;
import jdkx.lang.model.type.DeclaredType;
import jdkx.lang.model.type.ErrorType;
import jdkx.lang.model.type.ExecutableType;
import jdkx.lang.model.type.IntersectionType;
import jdkx.lang.model.type.NoType;
import jdkx.lang.model.type.NullType;
import jdkx.lang.model.type.PrimitiveType;
import jdkx.lang.model.type.TypeMirror;
import jdkx.lang.model.type.TypeVariable;
import jdkx.lang.model.type.UnionType;
import jdkx.lang.model.type.WildcardType;
import jdkx.lang.model.util.AbstractTypeVisitor8;

public class ShortTypePrinter extends AbstractTypeVisitor8<String, Void> {
  public static final ShortTypePrinter NO_PACKAGE = new ShortTypePrinter("*");

  private final String packageContext;

  private ShortTypePrinter(String packageContext) {
    this.packageContext = packageContext;
  }

  @Override
  public String visitIntersection(IntersectionType t, Void aVoid) {
    return t.getBounds().stream().map(this::print).collect(Collectors.joining(" & "));
  }

  public String print(TypeMirror type) {
    return type.accept(new ShortTypePrinter(packageContext), null);
  }

  @Override
  public String visitUnion(UnionType t, Void aVoid) {
    return t.getAlternatives().stream().map(this::print).collect(Collectors.joining(" | "));
  }

  @Override
  public String visitPrimitive(PrimitiveType t, Void aVoid) {
    return t.toString();
  }

  @Override
  public String visitNull(NullType t, Void aVoid) {
    return t.toString();
  }

  @Override
  public String visitArray(ArrayType t, Void aVoid) {
    return print(t.getComponentType()) + "[]";
  }

  @Override
  public String visitDeclared(DeclaredType t, Void aVoid) {
    String result = t.asElement().toString();

    if (!t.getTypeArguments().isEmpty()) {
      String params =
          t.getTypeArguments().stream().map(this::print).collect(Collectors.joining(", "));

      result += "<" + params + ">";
    }

    if (packageContext.equals("*")) return result.substring(result.lastIndexOf('.') + 1);
    else if (result.startsWith("java.lang")) return result.substring("java.lang.".length());
    else if (result.startsWith("java.util")) return result.substring("java.util.".length());
    else if (result.startsWith(packageContext)) return result.substring(packageContext.length());
    else return result;
  }

  @Override
  public String visitError(ErrorType t, Void aVoid) {
    return "_";
  }

  @Override
  public String visitTypeVariable(TypeVariable t, Void aVoid) {
    return t.asElement().toString();
  }

  @Override
  public String visitWildcard(WildcardType t, Void aVoid) {
    String result = "?";
    if (t.getSuperBound() != null) {
      result += " super " + print(t.getSuperBound());
    }

    if (t.getExtendsBound() != null) {
      result += " extends " + print(t.getExtendsBound());
    }

    return result;
  }

  @Override
  public String visitExecutable(ExecutableType t, Void aVoid) {
    return t.toString();
  }

  @Override
  public String visitNoType(NoType t, Void aVoid) {
    return t.toString();
  }
}

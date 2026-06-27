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

import androidx.annotation.NonNull;
import com.squareup.javapoet.ImportCollectingCodeWriter;
import com.squareup.javapoet.TypeName;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import jdkx.lang.model.element.ExecutableElement;
import jdkx.lang.model.element.TypeElement;
import jdkx.lang.model.element.VariableElement;
import jdkx.lang.model.type.TypeKind;
import jdkx.lang.model.type.TypeMirror;
import jdkx.lang.model.util.Types;
import openjdk.source.util.JavacTask;

/**
 * @author Akash Yadav
 */
public class MethodPtr {

  public String className, methodName;
  public String[] erasedParameterTypes;
  public String[] simplifiedErasedParameterTypes;

  public MethodPtr(@NonNull JavacTask task, @NonNull ExecutableElement method) {
    final Types types = task.getTypes();
    final TypeElement parent = (TypeElement) method.getEnclosingElement();
    className = parent.getQualifiedName().toString();
    methodName = method.getSimpleName().toString();
    erasedParameterTypes = new String[method.getParameters().size()];
    simplifiedErasedParameterTypes = new String[erasedParameterTypes.length];

    for (int i = 0; i < erasedParameterTypes.length; i++) {
      final VariableElement param = method.getParameters().get(i);
      final TypeMirror type = param.asType();
      final TypeMirror erased = types.erasure(type);
      erasedParameterTypes[i] = erased.toString();
      simplifiedErasedParameterTypes[i] = simplify(erased);
    }
  }

  private String simplify(@NonNull TypeMirror type) {

    if (type.getKind() == TypeKind.NULL) {
      return type.toString();
    }

    final TypeName name = TypeName.get(type);
    try {
      return getSimpleName(name);
    } catch (IOException e) {
      return type.toString();
    }
  }

  @NonNull
  private String getSimpleName(TypeName name) throws IOException {
    final StringBuilder sb = new StringBuilder();
    final ImportCollectingCodeWriter writer = new ImportCollectingCodeWriter(sb);
    writer.setPrintQualifiedNames(false);
    writer.emit(name);
    return sb.toString();
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(className, methodName);
    result = 31 * result + Arrays.hashCode(erasedParameterTypes);
    result = 31 * result + Arrays.hashCode(simplifiedErasedParameterTypes);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MethodPtr)) {
      return false;
    }
    MethodPtr methodPtr = (MethodPtr) o;
    return Objects.equals(className, methodPtr.className)
        && Objects.equals(methodName, methodPtr.methodName)
        && Arrays.equals(erasedParameterTypes, methodPtr.erasedParameterTypes)
        && Arrays.equals(simplifiedErasedParameterTypes, methodPtr.simplifiedErasedParameterTypes);
  }
}

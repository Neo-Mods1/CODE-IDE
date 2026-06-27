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



package com.neo.ide.lsp.java.utils

import com.neo.ide.preferences.utils.indentationString
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ImportCollectingCodeWriter
import com.squareup.javapoet.MethodSpec
import jdkx.lang.model.element.ExecutableElement
import jdkx.lang.model.element.Modifier
import jdkx.lang.model.type.DeclaredType
import jdkx.lang.model.type.NoType
import jdkx.lang.model.type.NullType
import jdkx.lang.model.type.TypeKind
import jdkx.lang.model.util.Types

/** @author Akash Yadav */
class JavaPoetUtils {
  companion object {
    @JvmStatic
    fun print(
      method: MethodSpec,
      importsOut: MutableSet<String>,
      qualifiedNames: Boolean = true,
    ): String {
      val sb = StringBuilder()
      val writer = ImportCollectingCodeWriter(sb, indentationString, emptySet(), emptySet())

      writer.isPrintQualifiedNames = qualifiedNames
      writer.emit(method)
      importsOut.addAll(writer.importClasses)

      return sb.toString()
    }

    @JvmStatic
    fun print(build: MethodSpec, imports: MutableSet<String>): String {
      return print(build, imports, true)
    }

    @JvmStatic
    fun buildMethod(
      method: ExecutableElement,
      types: Types,
      type: DeclaredType
    ): MethodSpec.Builder {
      val builder = MethodSpec.overriding(method, type, types)
      val mirrors = method.annotationMirrors
      if (mirrors != null && mirrors.isNotEmpty()) {
        for (mirror in mirrors) {
          if (mirror !is NullType && mirror.annotationType.kind != TypeKind.NULL) {
            builder.addAnnotation(AnnotationSpec.get(mirror))
          }
        }
      }
      var addComment = true
      // Add super call if the method is not abstract
      if (!method.modifiers.contains(Modifier.ABSTRACT)) {
        if (method.returnType is NoType) {
          builder.addStatement(createSuperCall(builder))
        } else {
          addComment = false
          builder.addComment("TODO: Implement this method")
          builder.addStatement("return " + createSuperCall(builder))
        }
      }
      if (addComment) {
        builder.addComment("TODO: Implement this method")
      }
      return builder
    }

    /**
     * Create a superclass method invocation statement.
     *
     * @param builder The method builder.
     * @return The super invocation statement string without ending ';'.
     */
    private fun createSuperCall(builder: MethodSpec.Builder): String {
      val sb = java.lang.StringBuilder()
      sb.append("super.")
      sb.append(builder.name)
      sb.append("(")
      for (i in builder.parameters.indices) {
        sb.append(builder.parameters[i].name)
        if (i != builder.parameters.size - 1) {
          sb.append(", ")
        }
      }
      sb.append(")")
      return sb.toString()
    }
  }
}

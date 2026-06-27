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

/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.neo.ide.templates.api.base.modules

import com.neo.ide.templates.api.base.ModuleTemplateBuilder
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import jdkx.lang.model.element.Modifier

internal fun ModuleTemplateBuilder.dependencies(): String {
  if (dependencies.isEmpty() && platforms.isEmpty()) {
    return ""
  }

  return StringBuilder().apply {
    append("dependencies {")
    append(System.lineSeparator())
    for (dep in platforms) {
      append(System.lineSeparator())
      append("    ${dep.platformValue()}")
    }
    append(System.lineSeparator())
    for (dep in dependencies) {
      append(System.lineSeparator())
      append("    ${dep.value()}")
    }
    append(System.lineSeparator())
    append("}")
  }.toString()
}

/**
 * Creates a new [FieldSpec] for a static constant and adds it to this [TypeSpec].
 *
 * @param name The name of the field.
 * @param configure A function to configure the field.
 */
inline fun TypeSpec.Builder.createConstant(type: TypeName, name: String, isPublic: Boolean,
                                    crossinline configure: FieldSpec.Builder.() -> Unit
) {
  val accessMod = if (isPublic) Modifier.PUBLIC else Modifier.PRIVATE
  FieldSpec.builder(type, name, accessMod, Modifier.STATIC, Modifier.FINAL)
    .apply(configure)
    .build()
    .also { addField(it) }
}

/**
 * Creates a new [FieldSpec] and adds it to this [TypeSpec].
 *
 * @param name The name of the field.
 * @param configure A function to configure the field.
 */
inline fun TypeSpec.Builder.createField(type: TypeName, name: String, vararg modifiers: Modifier,
                                 crossinline configure: FieldSpec.Builder.() -> Unit
) {
  FieldSpec.builder(type, name, *modifiers).apply(configure).build().also { addField(it) }
}

/**
 * Creates a new [MethodSpec] for a constructor and adds it to this [TypeSpec].
 *
 * @param configure A function to configure the method.
 */
inline fun TypeSpec.Builder.createConstructor(crossinline configure: MethodSpec.Builder.() -> Unit) {
  MethodSpec.constructorBuilder().apply(configure).build().also { addMethod(it) }
}

/**
 * Creates a new [MethodSpec] and adds it to this [TypeSpec].
 *
 * @param name The name of the method.
 * @param configure A function to configure the method.
 */
inline fun TypeSpec.Builder.createMethod(name: String, crossinline configure: MethodSpec.Builder.() -> Unit) {
  MethodSpec.methodBuilder(name).apply(configure).build().also { addMethod(it) }
}
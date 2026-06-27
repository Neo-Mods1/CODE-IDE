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

package com.neo.ide.templates.impl

import com.neo.ide.app.BaseApplication
import com.neo.ide.templates.api.ParameterConstraint
import com.neo.ide.templates.api.ParameterConstraint.CLASS
import com.neo.ide.templates.api.ParameterConstraint.CLASS_NAME
import com.neo.ide.templates.api.ParameterConstraint.DIRECTORY
import com.neo.ide.templates.api.ParameterConstraint.EXISTS
import com.neo.ide.templates.api.ParameterConstraint.FILE
import com.neo.ide.templates.api.ParameterConstraint.LAYOUT
import com.neo.ide.templates.api.ParameterConstraint.MODULE_NAME
import com.neo.ide.templates.api.ParameterConstraint.NONEMPTY
import com.neo.ide.templates.api.ParameterConstraint.PACKAGE
import com.neo.ide.templates.api.base.util.isValidModuleName
import com.neo.ide.templates.impl.R.string
import com.neo.ide.utils.AndroidUtils
import jdkx.lang.model.SourceVersion
import java.io.File

/**
 * Verifies constraint for text field widget.
 *
 * @author Akash Yadav
 */
object ConstraintVerifier {

  /**
   * Checks whether the given input is valid against the given constraints.
   *
   * @see verify
   */
  fun isValid(input: String, constraints: List<ParameterConstraint>): Boolean =
    verify(input, constraints) == null

  /**
   * Verify the input against the given constraints.
   *
   * @param input The input to verify.
   * @param constraints The constraints to verify against.
   * @return The error message if the validation fails, `null` otherwise.
   */
  fun verify(input: String, constraints: List<ParameterConstraint>): String? {
    var err: String?
    for (constraint in constraints) {
      err = when (constraint) {
        NONEMPTY -> checkNotEmpty(input)
        PACKAGE -> validatePackageName(input)
        CLASS -> validateClassName(input)
        CLASS_NAME -> validateSimpleName(input)
        MODULE_NAME -> validateModuleName(input)
        LAYOUT -> validateLayoutName(input)
        EXISTS -> checKFileExists(input)
        FILE -> checkIsFile(input)
        DIRECTORY -> checkIsDirectory(input)
        else -> throw IllegalArgumentException(
          "Unsupported parameter constraint '$constraint'")
      }

      if (err != null) {
        return err
      }
    }
    return null
  }

  private fun validatePackageName(input: String): String? {
    AndroidUtils.validatePackageName(input)?.let { return it }

    if (SourceVersion.isName(input)) {
      return null
    }

    return BaseApplication.getBaseInstance()
      .getString(R.string.msg_package_is_not_valid)
  }

  private fun validateLayoutName(input: String): String? {
    if (input.isBlank()) {
      return BaseApplication.getBaseInstance()
        .getString(R.string.msg_value_empty)
    }

    // Allow only lowecase letters with digits and underscores
    if (input.matches(Regex("[a-z][a-z0-9_]+"))) {
      return null
    }

    return BaseApplication.getBaseInstance()
      .getString(string.msg_invalid_layout_name)
  }

  private fun checkIsDirectory(input: String): String? {
    checKFileExists(input)?.let { return it }

    val file = File(input)
    if (file.isDirectory) {
      return null
    }

    return BaseApplication.getBaseInstance()
      .getString(string.msg_path_must_be_dir)
  }

  private fun checkIsFile(input: String): String? {
    checKFileExists(input)?.let { return it }

    val file = File(input)
    if (file.isFile) {
      return null
    }

    return BaseApplication.getBaseInstance()
      .getString(string.msg_path_must_be_file)
  }

  private fun checKFileExists(input: String): String? {
    val file = File(input)
    if (file.exists()) {
      return null
    }

    return BaseApplication.getBaseInstance()
      .getString(string.msg_file_not_exist)
  }

  private fun checkNotEmpty(input: String): String? {
    if (input.isNotBlank()) {
      return null
    }

    return BaseApplication.getBaseInstance().getString(string.msg_value_empty)
  }

  private fun validateModuleName(input: String): String? {
    if (isValidModuleName(input)) {
      return null
    }

    return BaseApplication.getBaseInstance()
      .getString(string.msg_invalid_module_name)
  }

  private fun validateClassName(name: String): String? {
    if (!name.contains('.')) {
      return validateSimpleName(name)
    }

    val pck = name.substring(0, name.lastIndexOf('.'))
    var err: String? = null
    if (pck.contains('.')) {
      err = validatePackageName(pck)
    } else {
      if (!SourceVersion.isIdentifier(pck) || SourceVersion.isKeyword(pck)) {
        err = BaseApplication.getBaseInstance()
          .getString(string.msg_package_is_not_valid)
      }
    }

    if (err != null) {
      return err
    }

    return validateSimpleName(name.substring(name.lastIndexOf('.') + 1))
  }

  private fun validateSimpleName(name: String): String? {
    if (SourceVersion.isKeyword(name) || !SourceVersion.isIdentifier(name)) {
      return BaseApplication.getBaseInstance()
        .getString(string.msg_classname_with_keywords)
    }
    return null
  }
}
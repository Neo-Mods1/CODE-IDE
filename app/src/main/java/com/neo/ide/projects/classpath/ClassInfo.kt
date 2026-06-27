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

package com.neo.ide.projects.classpath

/**
 * Information about a class in a JAR file.
 *
 * @author Akash Yadav
 */
@Suppress("DataClassPrivateConstructor")
data class ClassInfo
private constructor(
  val name: String,
  val simpleName: String,
  val packageName: String,
  val isTopLevel: Boolean,
  val isAnonymous: Boolean,
  val isLocal: Boolean,
  val isInner: Boolean
) {

  companion object {

    @JvmStatic
    fun create(name: String): ClassInfo? {
      val isTopLevel = name.indexOf('$') == -1

      val simpleName =
        if (!isTopLevel) {
          name.substringAfterLast('$')
        } else if (name.indexOf('.') != -1) {
          name.substringAfterLast('.')
        } else {
          name
        }

      if (simpleName.isBlank()) {
        return null
      }

      val packageName =
        if (name.contains('.')) {
          name.substringBeforeLast('.')
        } else {
          name
        }

      val isAnonymous = simpleName.isDigitsOnly()
      val isLocal = simpleName[0].isDigit() && simpleName.contains(Regex("[A-Za-z]"))
      val isInner = !isTopLevel && !isLocal && !isAnonymous

      return ClassInfo(name, simpleName, packageName, isTopLevel, isAnonymous, isLocal, isInner)
    }

    private fun CharSequence.isDigitsOnly(): Boolean {
      for (char in this) {
        if (!char.isDigit()) {
          return false
        }
      }

      return true
    }
  }
}

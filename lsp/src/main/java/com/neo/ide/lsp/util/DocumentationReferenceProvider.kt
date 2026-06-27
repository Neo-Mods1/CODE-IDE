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



package com.neo.ide.lsp.util

import com.neo.ide.lsp.models.ClassCompletionData
import com.neo.ide.lsp.models.ICompletionData
import com.neo.ide.lsp.models.MemberCompletionData
import com.neo.ide.lsp.models.MethodCompletionData
import org.slf4j.LoggerFactory

/**
 * Provides the documentation URL for classes, methods, fields, etc.
 *
 * @author Akash Yadav
 */
object DocumentationReferenceProvider {

  private val log = LoggerFactory.getLogger(DocumentationReferenceProvider::class.java)

  const val DOCS_BASE_URL = "https://developer.android.com/reference/"

  /**
   * Package names whose documentation is most likely to be available on the Android Developers
   * website.
   */
  private val availablePackages =
    setOf(
      "android", // Android APIs
      "androidx", // AndroidX libraries
      "com.google.android.material", // Material Components
      "java" // Java APIs
    )

  /**
   * Get the documentation URL for given completion data.
   *
   * @return The URL or `null` if cannot be determined.
   */
  @JvmStatic
  fun getUrl(data: ICompletionData): String? {
    val klass =
      when (data) {
        is ClassCompletionData -> data
        is MemberCompletionData -> data.classInfo
        else -> return null
      }
    val url = StringBuilder(DOCS_BASE_URL)
    val baseName =
      if (klass.isNested) {
        klass.topLevelClass
      } else klass.className

    if (availablePackages.find { baseName.startsWith("$it.") } == null) {
      // This package is probably not listed on Android Developers documentation
      return null
    }

    url.append(baseName.replace('.', '/'))

    if (klass.isNested) {
      url.append('.')
      url.append(klass.nameWithoutTopLevel)
    }

    if (data is MemberCompletionData) {
      url.append('#')
      url.append(data.memberName)
    }

    if (data is MethodCompletionData) {
      url.append('(')
      url.append(data.parameterTypes.joinToString(separator = ", "))
      url.append(')')
    }

    log.debug("Documentation URL for {}#{} is {}", klass.className,
      ((data as? MemberCompletionData?)?.memberName ?: "<self>"), url)

    return url.toString()
  }
}

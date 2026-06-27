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

package com.neo.ide.templates.impl.base
import com.neo.ide.templates.api.Language
import com.neo.ide.templates.api.base.AndroidModuleTemplateBuilder
import com.neo.ide.templates.api.base.modules.android.ManifestActivity
import com.neo.ide.templates.api.base.util.SourceWriter
import com.neo.ide.templates.api.base.util.withXmlDecl
internal inline fun AndroidModuleTemplateBuilder.writeMainActivity(
  writer: SourceWriter, crossinline ktSrc: () -> String, crossinline javaSrc: () -> String
) {
  val className = "MainActivity"
  writer.apply {
    if (data.language == Language.Kotlin) {
      val src = ktSrc()
      if (src.isNotBlank()) {
        writeKtSrc(data.packageName, className, source = src)
      }
    } else {
      val src = javaSrc()
      if (src.isNotBlank()) {
        writeJavaSrc(packageName = data.packageName, className = className,
          source = src)
      }
    }
  }
  manifest {
    addActivity(
      ManifestActivity(name = className, isExported = true, isLauncher = true))
  }
}
internal fun emptyValuesFile(): String {
  return """
<resources></resources>
  """.trim().withXmlDecl()
}

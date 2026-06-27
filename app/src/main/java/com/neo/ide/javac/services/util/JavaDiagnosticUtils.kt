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



package com.neo.ide.javac.services.util

import jdkx.tools.Diagnostic
import jdkx.tools.JavaFileObject
import openjdk.tools.javac.api.ClientCodeWrapper
import openjdk.tools.javac.util.JCDiagnostic

/** @author Akash Yadav */
class JavaDiagnosticUtils {
  companion object {
    @JvmStatic
    fun asJCDiagnostic(diagnostic: Diagnostic<out JavaFileObject>): JCDiagnostic? {
      if (diagnostic is JCDiagnostic) {
        return diagnostic
      } else if (diagnostic is ClientCodeWrapper.DiagnosticSourceUnwrapper) {
        return diagnostic.d
      }

      return null
    }

    @JvmStatic
    fun asUnwrapper(
      diagnostic: Diagnostic<out JavaFileObject>
    ): ClientCodeWrapper.DiagnosticSourceUnwrapper? {
      if (diagnostic is ClientCodeWrapper.DiagnosticSourceUnwrapper) {
        return diagnostic
      } else if (diagnostic is JCDiagnostic) {
        return wrap(diagnostic)
      }

      return null
    }

    private fun wrap(diagnostic: JCDiagnostic): ClientCodeWrapper.DiagnosticSourceUnwrapper {
      val klass = ClientCodeWrapper.DiagnosticSourceUnwrapper::class.java
      val construct = klass.getDeclaredConstructor(JCDiagnostic::class.java)
      construct.isAccessible = true
      return construct.newInstance(diagnostic)
    }
  }
}

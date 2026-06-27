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

package com.neo.ide.tooling.api.util

import com.neo.ide.utils.AndroidPluginVersion

/**
 * System properties for configuring the toolign API.
 *
 * @author Akash Yadav
 */
object ToolingProps {

  val TESTING_IS_TEST_ENV = propName("testing", "isTestEnv")
  val TESTING_LATEST_AGP_VERSION = propName("testing", "latestAgpVersion")

  val isTestEnv: Boolean
    get() = System.getProperty(TESTING_IS_TEST_ENV).toBoolean()

  val latestTestedAgpVersion: AndroidPluginVersion
    get() {
      if (!isTestEnv) {
        return AndroidPluginVersion.LATEST_TESTED
      }
      return System.getProperty(TESTING_LATEST_AGP_VERSION)?.let { AndroidPluginVersion.parse(it) }
        ?: AndroidPluginVersion.LATEST_TESTED
    }

  fun propName(cat: String, name: String) = "ide.tooling.$cat.$name"
}

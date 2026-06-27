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

package com.neo.ide.builder.model

import com.android.build.gradle.options.StringOption
import com.android.build.gradle.options.StringOption.AAPT2_FROM_MAVEN_OVERRIDE
import com.android.builder.model.v2.ide.SyncIssue

/**
 * Sync issue model for AndroidIDE.
 *
 * @author Akash Yadav
 */
interface IDESyncIssue : SyncIssue {
  companion object {

    /**
     * Indicates that the Android Gradle Plugin that is being used by the project
     * is too new for AndroidIDE. Data is `projectAgpVersion:maxAgpVersion`.
     */
    const val TYPE_AGP_VERSION_TOO_NEW = -1

    // Note: When adding new types, decrement the version by 1
    // The types that are defined in SyncIssue class have their values starting at 0 and incremented
    // by 1 when new types are added. So, we could never know what is the latest type's value
  }
}

/**
 * Checks whether this [SyncIssue] should be ignored and not reported to the user.
 *
 * @return Whether the issue can be ignored.
 */
fun SyncIssue.shouldBeIgnored() : Boolean {
  if (this.type != SyncIssue.TYPE_UNSUPPORTED_PROJECT_OPTION_USE) {
    return false
  }

  // AndroidIDE sets android.aapt2FromMavenOverride in order to use a custom AAPT2 that is
  // compatible with Android
  return AAPT2_FROM_MAVEN_OVERRIDE.propertyName == this.data
}

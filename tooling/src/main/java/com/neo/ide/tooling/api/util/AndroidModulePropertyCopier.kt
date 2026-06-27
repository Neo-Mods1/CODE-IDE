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

import com.android.builder.model.v2.ide.SourceProvider
import com.neo.ide.builder.model.DefaultSyncIssue
import com.neo.ide.builder.model.DefaultViewBindingOptions
import com.neo.ide.builder.model.IDESyncIssue

object AndroidModulePropertyCopier {

  @JvmStatic
  fun copy(syncIssue: IDESyncIssue): DefaultSyncIssue {
    return DefaultSyncIssue(
      data = syncIssue.data,
      message = syncIssue.message,
      multiLineMessage = syncIssue.multiLineMessage,
      severity = syncIssue.severity,
      type = syncIssue.type
    )
  }

  @JvmStatic
  fun copy(viewBindingOptions: Any?): DefaultViewBindingOptions {
    return DefaultViewBindingOptions()
  }
}

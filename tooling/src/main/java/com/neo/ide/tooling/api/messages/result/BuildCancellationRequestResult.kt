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

package com.neo.ide.tooling.api.messages.result

/**
 * Result for a build cancellation request.
 *
 * @param wasEnqueued `true`, if the cancellation request was enqueued,`false` otherwise.
 * @param failureReason The reason for the cancellation request enqueue failure.
 * @author Akash Yadav
 */
data class BuildCancellationRequestResult(
  val wasEnqueued: Boolean,
  val failureReason: Reason? = null
) {

  /** Reason for build cancellation request failure. */
  enum class Reason private constructor(var message: String) {
    NO_RUNNING_BUILD("No running builds"),
    CANCELLATION_ERROR("An error occurred performing the cancellation request"),
    UNKNOWN("Unknown error")
  }
}

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
 * Result for a task execution.
 *
 * @param isSuccessful The result of the task execution.
 * @param failure The type of failure. Non-null only if [isSuccessful] is `false`.
 *
 * @author Akash Yadav
 */
data class TaskExecutionResult(val isSuccessful: Boolean, val failure: Failure?) {

  companion object {

    /**
     * Result for a successful build.
     */
    @JvmStatic
    val SUCCESS = TaskExecutionResult(true, null)
  }

  enum class Failure {
    PROJECT_NOT_FOUND,
    PROJECT_NOT_INITIALIZED,
    PROJECT_NOT_DIRECTORY,
    PROJECT_DIRECTORY_INACCESSIBLE,
    UNKNOWN,
    UNSUPPORTED_GRADLE_VERSION,
    UNSUPPORTED_CONFIGURATION,
    UNSUPPORTED_BUILD_ARGUMENT,
    BUILD_FAILED,
    BUILD_CANCELLED,
    CONNECTION_ERROR,
    CONNECTION_CLOSED
  }
}

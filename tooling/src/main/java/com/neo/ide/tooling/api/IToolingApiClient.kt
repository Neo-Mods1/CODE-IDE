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

package com.neo.ide.tooling.api

import com.neo.ide.tooling.api.messages.LogMessageParams
import com.neo.ide.tooling.api.messages.result.BuildInfo
import com.neo.ide.tooling.api.messages.result.BuildResult
import com.neo.ide.tooling.api.messages.result.GradleWrapperCheckResult
import com.neo.ide.tooling.events.ProgressEvent
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment
import java.util.concurrent.*

/**
 * A client consumes services provided by [IToolingApiServer].
 *
 * @author Akash Yadav
 */
@JsonSegment("client")
interface IToolingApiClient {

  /**
   * Log the given log message.
   *
   * @param params The parameters to log the message.
   */
  @JsonNotification fun logMessage(params: LogMessageParams)

  /**
   * Log the build output received from Gradle.
   *
   * @param line The line of the build output to log.
   */
  @JsonNotification fun logOutput(line: String)

  /** Called just before a build is started. */
  @JsonNotification fun prepareBuild(buildInfo: BuildInfo)

  /**
   * Called when a build is successful.
   *
   * @param result The result containing the tasks that were run. Maybe an empty list if no tasks
   * were specified or if the build was not related to any tasks.
   */
  @JsonNotification fun onBuildSuccessful(result: BuildResult)

  /**
   * Called when a build fails.
   *
   * @param result The result containing the tasks that were run. Maybe an empty list if no tasks
   * were specified or if the build was not related to any tasks.
   */
  @JsonNotification fun onBuildFailed(result: BuildResult)

  /**
   * Called when a [ProgressEvent] is received from Gradle build.
   *
   * @param event The [ProgressEvent] model describing the event.
   */
  @JsonNotification fun onProgressEvent(event: ProgressEvent)

  /**
   * Get the extra build arguments that will be used for every build.
   *
   * @return The extra build arguments.
   */
  @JsonRequest fun getBuildArguments(): CompletableFuture<List<String>>

  /**
   * Tells the client to check if the Gradle wrapper files are available.
   *
   * @return A [CompletableFuture] which completes when the client is done checking the wrapper
   * availability. The future provides a result which tells if the wrapper is available or not.
   */
  @JsonRequest fun checkGradleWrapperAvailability(): CompletableFuture<GradleWrapperCheckResult>
}

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

import com.neo.ide.tooling.api.messages.InitializeProjectParams
import com.neo.ide.tooling.api.messages.TaskExecutionMessage
import com.neo.ide.tooling.api.messages.result.BuildCancellationRequestResult
import com.neo.ide.tooling.api.messages.result.InitializeResult
import com.neo.ide.tooling.api.messages.result.TaskExecutionResult
import com.neo.ide.tooling.api.models.ToolingServerMetadata
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment
import java.util.concurrent.CompletableFuture

/**
 * A tooling api server provides services related to the Gradle Tooling API.
 *
 * @author Akash Yadav
 */
@JsonSegment("server")
interface IToolingApiServer {

  /**
   * Returns the metadata about the tooling server.
   */
  @JsonRequest
  fun metadata(): CompletableFuture<ToolingServerMetadata>

  /** Initialize the server with the project directory. */
  @JsonRequest
  fun initialize(params: InitializeProjectParams): CompletableFuture<InitializeResult>

  /** Is the server initialized? */
  @JsonRequest
  fun isServerInitialized(): CompletableFuture<Boolean>

  /** Get the root project. */
  @JsonRequest
  fun getRootProject(): CompletableFuture<IProject>

  /** Execute the tasks specified in the message. */
  @JsonRequest
  fun executeTasks(message: TaskExecutionMessage): CompletableFuture<TaskExecutionResult>

  /**
   * Cancel the current build.
   *
   * @return A [CompletableFuture] which completes when the current build cancellation process
   * finishes (either successfully or with an error).
   */
  @JsonRequest
  fun cancelCurrentBuild(): CompletableFuture<BuildCancellationRequestResult>

  /**
   * Shutdown the tooling API server. This will disconnect all the project connection instances.
   *
   * @return A [CompletableFuture] which completes when the shutdown process is finished.
   */
  @JsonRequest
  fun shutdown(): CompletableFuture<Void>
}

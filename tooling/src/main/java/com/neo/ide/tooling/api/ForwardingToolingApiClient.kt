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
import java.util.concurrent.CompletableFuture

/**
 * A [IToolingApiClient] which forwards all of its calls to the given client.
 *
 * @author Akash Yadav
 */
class ForwardingToolingApiClient(var client: IToolingApiClient?) : IToolingApiClient {

  override fun logMessage(params: LogMessageParams) {
    client?.logMessage(params)
  }

  override fun logOutput(line: String) {
    client?.logOutput(line)
  }

  override fun prepareBuild(buildInfo: BuildInfo) {
    client?.prepareBuild(buildInfo)
  }

  override fun onBuildSuccessful(result: BuildResult) {
    client?.onBuildSuccessful(result)
  }

  override fun onBuildFailed(result: BuildResult) {
    client?.onBuildFailed(result)
  }

  override fun onProgressEvent(event: ProgressEvent) {
    client?.onProgressEvent(event)
  }

  override fun getBuildArguments(): CompletableFuture<List<String>> {
    return client?.getBuildArguments() ?: CompletableFuture.completedFuture(emptyList())
  }

  override fun checkGradleWrapperAvailability(): CompletableFuture<GradleWrapperCheckResult> {
    return client?.checkGradleWrapperAvailability()
      ?: CompletableFuture.completedFuture(GradleWrapperCheckResult(false))
  }
}

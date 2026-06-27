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

package com.neo.ide.tooling.impl.progress

import com.neo.ide.tooling.api.IToolingApiClient
import com.neo.ide.tooling.impl.Main
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.ProgressEvent
import org.gradle.tooling.events.ProgressListener
import org.gradle.tooling.events.StartEvent
import org.gradle.tooling.events.StatusEvent
import org.gradle.tooling.events.configuration.ProjectConfigurationFinishEvent
import org.gradle.tooling.events.configuration.ProjectConfigurationProgressEvent
import org.gradle.tooling.events.configuration.ProjectConfigurationStartEvent
import org.gradle.tooling.events.download.FileDownloadOperationDescriptor
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskProgressEvent
import org.gradle.tooling.events.task.TaskStartEvent
import org.gradle.tooling.events.test.TestFinishEvent
import org.gradle.tooling.events.test.TestProgressEvent
import org.gradle.tooling.events.test.TestStartEvent
import org.gradle.tooling.events.transform.TransformFinishEvent
import org.gradle.tooling.events.transform.TransformProgressEvent
import org.gradle.tooling.events.transform.TransformStartEvent
import org.gradle.tooling.events.work.WorkItemFinishEvent
import org.gradle.tooling.events.work.WorkItemProgressEvent
import org.gradle.tooling.events.work.WorkItemStartEvent

/**
 * A [ProgressListener] which forwards all of its event to [IToolingApiClient].
 * @author Akash Yadav
 */
class ForwardingProgressListener : ProgressListener {

  override fun statusChanged(event: ProgressEvent?) {
    if (event == null || Main.client == null) {
      return
    }

    // File download progress event must not be sent
    if (event.descriptor is FileDownloadOperationDescriptor) {
      return
    }

    val ideEvent: com.neo.ide.tooling.events.ProgressEvent =
      when (event) {
        is ProjectConfigurationProgressEvent ->
          when (event) {
            is ProjectConfigurationStartEvent -> EventTransformer.projectConfigurationStart(event)
            is ProjectConfigurationFinishEvent -> EventTransformer.projectConfigurationFinish(event)
            else -> EventTransformer.projectConfigurationProgress(event)
          }

        is TaskProgressEvent ->
          when (event) {
            is TaskStartEvent -> EventTransformer.taskStart(event)
            is TaskFinishEvent -> EventTransformer.taskFinish(event)
            else -> EventTransformer.taskProgress(event)
          }

        is TestProgressEvent ->
          when (event) {
            is TestStartEvent -> EventTransformer.testStart(event)
            is TestFinishEvent -> EventTransformer.testFinish(event)
            else -> EventTransformer.testProgress(event)
          }

        is TransformProgressEvent ->
          when (event) {
            is TransformStartEvent -> EventTransformer.transformStart(event)
            is TransformFinishEvent -> EventTransformer.transformFinish(event)
            else -> EventTransformer.transformProgress(event)
          }

        is WorkItemProgressEvent ->
          when (event) {
            is WorkItemStartEvent -> EventTransformer.workStart(event)
            is WorkItemFinishEvent -> EventTransformer.workFinish(event)
            else -> EventTransformer.workProgress(event)
          }

        is StatusEvent -> EventTransformer.statusEvent(event)
        else ->
          when (event) {
            is StartEvent -> EventTransformer.start(event)
            is FinishEvent -> EventTransformer.finish(event)
            else -> EventTransformer.progress(event)
          }
      }

    Main.client.onProgressEvent(ideEvent)
  }
}

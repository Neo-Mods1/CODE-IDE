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

import com.neo.ide.tooling.events.StatusEvent
import com.neo.ide.tooling.events.configuration.ProjectConfigurationOperationResult.PluginApplicationResult
import com.neo.ide.tooling.events.download.FileDownloadFinishEvent
import com.neo.ide.tooling.events.download.FileDownloadProgressEvent
import com.neo.ide.tooling.events.download.FileDownloadStartEvent
import com.neo.ide.tooling.events.internal.DefaultFinishEvent
import com.neo.ide.tooling.events.internal.DefaultOperationDescriptor
import com.neo.ide.tooling.events.internal.DefaultOperationResult
import com.neo.ide.tooling.events.internal.DefaultProgressEvent
import com.neo.ide.tooling.events.internal.DefaultStartEvent
import com.neo.ide.tooling.events.task.TaskFinishEvent
import com.neo.ide.tooling.events.task.TaskProgressEvent
import com.neo.ide.tooling.events.task.TaskStartEvent
import com.neo.ide.tooling.events.test.TestFinishEvent
import com.neo.ide.tooling.events.test.TestOperationResult
import com.neo.ide.tooling.events.test.TestProgressEvent
import com.neo.ide.tooling.events.test.TestStartEvent
import com.neo.ide.tooling.events.transform.TransformFinishEvent
import com.neo.ide.tooling.events.transform.TransformOperationDescriptor.SubjectDescriptor
import com.neo.ide.tooling.events.transform.TransformOperationResult
import com.neo.ide.tooling.events.transform.TransformStartEvent
import com.neo.ide.tooling.events.work.WorkItemFinishEvent
import com.neo.ide.tooling.events.work.WorkItemOperationResult
import com.neo.ide.tooling.events.work.WorkItemProgressEvent
import com.neo.ide.tooling.events.work.WorkItemStartEvent
import com.neo.ide.tooling.model.PluginIdentifier
import org.gradle.tooling.events.OperationDescriptor
import org.gradle.tooling.events.ProgressEvent
import org.gradle.tooling.events.configuration.ProjectConfigurationFinishEvent
import org.gradle.tooling.events.configuration.ProjectConfigurationOperationDescriptor
import org.gradle.tooling.events.configuration.ProjectConfigurationOperationResult
import org.gradle.tooling.events.configuration.ProjectConfigurationProgressEvent
import org.gradle.tooling.events.configuration.ProjectConfigurationStartEvent
import org.gradle.tooling.events.configuration.ProjectConfigurationSuccessResult
import org.gradle.tooling.events.download.FileDownloadOperationDescriptor
import org.gradle.tooling.events.download.FileDownloadResult
import org.gradle.tooling.events.task.TaskExecutionResult
import org.gradle.tooling.events.task.TaskFailureResult
import org.gradle.tooling.events.task.TaskOperationDescriptor
import org.gradle.tooling.events.task.TaskOperationResult
import org.gradle.tooling.events.task.TaskSkippedResult
import org.gradle.tooling.events.task.TaskSuccessResult
import org.gradle.tooling.events.test.TestOperationDescriptor
import org.gradle.tooling.events.test.TestSuccessResult
import org.gradle.tooling.events.transform.TransformOperationDescriptor
import org.gradle.tooling.events.transform.TransformSuccessResult
import org.gradle.tooling.events.work.WorkItemOperationDescriptor
import org.gradle.tooling.events.work.WorkItemSuccessResult
import org.gradle.tooling.model.ProjectIdentifier

/** @author Akash Yadav */
class EventTransformer {
  companion object {

    // ------------------------ COMMON -------------------------
    private fun operationDescriptor(
      descriptor: OperationDescriptor?
    ): com.neo.ide.tooling.events.OperationDescriptor? =
      when (descriptor) {
        null -> null
        is ProjectConfigurationOperationDescriptor -> projectConfigurationDescriptor(descriptor)
        is FileDownloadOperationDescriptor -> fileDownloadDescriptor(descriptor)
        is TaskOperationDescriptor -> taskDescriptor(descriptor)
        is TransformOperationDescriptor -> transformDescriptor(descriptor)
        is WorkItemOperationDescriptor -> workDescriptor(descriptor)
        else ->
          DefaultOperationDescriptor(name = descriptor.name, displayName = descriptor.displayName)
      }

    // ----------------- PROJECT CONFIGURATION --------------------
    @JvmStatic
    fun projectConfigurationStart(
      event: ProjectConfigurationStartEvent
    ): com.neo.ide.tooling.events.configuration.ProjectConfigurationProgressEvent =
      com.neo.ide.tooling.events.configuration.ProjectConfigurationStartEvent(
        displayName = event.displayName,
        eventTime = event.eventTime,
        descriptor = projectConfigurationDescriptor(event.descriptor)
      )

    @JvmStatic
    fun projectConfigurationProgress(
      event: ProjectConfigurationProgressEvent
    ): com.neo.ide.tooling.events.configuration.ProjectConfigurationProgressEvent =
      com.neo.ide.tooling.events.configuration.ProjectConfigurationProgressEvent(
        displayName = event.displayName,
        eventTime = event.eventTime,
        descriptor = projectConfigurationDescriptor(event.descriptor)
      )

    @JvmStatic
    fun projectConfigurationFinish(
      event: ProjectConfigurationFinishEvent
    ): com.neo.ide.tooling.events.configuration.ProjectConfigurationProgressEvent =
      com.neo.ide.tooling.events.configuration.ProjectConfigurationFinishEvent(
        displayName = event.displayName,
        eventTime = event.eventTime,
        descriptor = projectConfigurationDescriptor(event.descriptor),
        result = projectConfigurationResult(event.result)
      )

    private fun projectConfigurationResult(
      result: ProjectConfigurationOperationResult
    ): com.neo.ide.tooling.events.configuration.ProjectConfigurationOperationResult =
      com.neo.ide.tooling.events.configuration.ProjectConfigurationOperationResult(
        pluginApplicationResults =
          result.pluginApplicationResults.map {
            PluginApplicationResult(
              plugin = PluginIdentifier(it.plugin?.displayName ?: "Unknown plugin"),
              it.totalConfigurationTime.toMillis()
            )
          },
        startTime = result.startTime,
        endTime = result.endTime,
        success = result is ProjectConfigurationSuccessResult
      )

    private fun projectConfigurationDescriptor(
      descriptor: ProjectConfigurationOperationDescriptor
    ): com.neo.ide.tooling.events.configuration.ProjectConfigurationOperationDescriptor =
      com.neo.ide.tooling.events.configuration.ProjectConfigurationOperationDescriptor(
        project = projectIdentifier(descriptor.project),
        name = descriptor.name,
        displayName = descriptor.displayName
      )

    private fun projectIdentifier(
      project: ProjectIdentifier
    ): com.neo.ide.tooling.model.ProjectIdentifier =
      com.neo.ide.tooling.model.ProjectIdentifier(
        projectPath = project.projectPath,
        buildIdentifier =
          com.neo.ide.tooling.model.BuildIdentifier(project.buildIdentifier.rootDir)
      )

    // ---------------------- FILE DOWNLOAD ---------------------------------
    @JvmStatic
    fun fileDownloadStart(
      event: org.gradle.tooling.events.download.FileDownloadStartEvent
    ): FileDownloadStartEvent =
      FileDownloadStartEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        descriptor = fileDownloadDescriptor(event.descriptor)
      )

    @JvmStatic
    fun fileDownloadProgress(
      event: org.gradle.tooling.events.download.FileDownloadProgressEvent
    ): FileDownloadProgressEvent =
      FileDownloadProgressEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        descriptor = fileDownloadDescriptor(event.descriptor)
      )

    @JvmStatic
    fun fileDownloadFinish(
      event: org.gradle.tooling.events.download.FileDownloadFinishEvent
    ): FileDownloadFinishEvent =
      FileDownloadFinishEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        descriptor = fileDownloadDescriptor(event.descriptor),
        result = fileDownloadResult(event.result)
      )

    private fun fileDownloadResult(
      result: FileDownloadResult
    ): com.neo.ide.tooling.events.download.FileDownloadResult =
      com.neo.ide.tooling.events.download.FileDownloadResult(
        bytesDownloaded = result.bytesDownloaded,
        startTime = result.startTime,
        endTime = result.endTime
      )

    private fun fileDownloadDescriptor(
      descriptor: FileDownloadOperationDescriptor
    ): com.neo.ide.tooling.events.download.FileDownloadOperationDescriptor =
      com.neo.ide.tooling.events.download.FileDownloadOperationDescriptor(
        descriptor.uri,
        descriptor.name,
        descriptor.displayName
      )

    // -------------------- TASK -------------------------------
    @JvmStatic
    fun taskStart(event: org.gradle.tooling.events.task.TaskStartEvent): TaskStartEvent =
      TaskStartEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        descriptor = taskDescriptor(event.descriptor)
      )

    @JvmStatic
    fun taskProgress(event: org.gradle.tooling.events.task.TaskProgressEvent): TaskProgressEvent =
      TaskProgressEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        descriptor = taskDescriptor(event.descriptor)
      )

    @JvmStatic
    fun taskFinish(event: org.gradle.tooling.events.task.TaskFinishEvent): TaskFinishEvent =
      TaskFinishEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        descriptor = taskDescriptor(event.descriptor),
        result = taskResult(event.result)
      )

    private fun taskResult(
      result: TaskOperationResult
    ): com.neo.ide.tooling.events.task.TaskOperationResult {

      // The order of conditions must not change here.

      if (result is TaskSuccessResult) {
        return com.neo.ide.tooling.events.task.TaskSuccessResult(
          result.isUpToDate,
          result.isFromCache,
          result.startTime,
          result.endTime,
          result.isIncremental,
          result.executionReasons
        )
      }

      if (result is TaskFailureResult) {
        return com.neo.ide.tooling.events.task.TaskFailureResult(
          result.startTime,
          result.endTime
        )
      }

      if (result is TaskExecutionResult) {
        return com.neo.ide.tooling.events.task.TaskExecutionResult(
          result.startTime,
          result.endTime,
          result.isIncremental,
          result.executionReasons
        )
      }

      if (result is TaskSkippedResult) {
        return com.neo.ide.tooling.events.task.TaskSkippedResult(
          result.skipMessage,
          result.startTime,
          result.endTime
        )
      }

      return com.neo.ide.tooling.events.task.TaskOperationResult(
        startTime = result.startTime,
        endTime = result.endTime
      )
    }

    private fun taskDescriptor(
      descriptor: TaskOperationDescriptor
    ): com.neo.ide.tooling.events.task.TaskOperationDescriptor =
      com.neo.ide.tooling.events.task.TaskOperationDescriptor(
        dependencies =
          descriptor.dependencies.filterNotNull().mapNotNull { operationDescriptor(it) }.toSet(),
        originPlugin = PluginIdentifier(descriptor.originPlugin?.displayName ?: "Unknown plugin"),
        taskPath = descriptor.taskPath,
        name = descriptor.name,
        displayName = descriptor.displayName
      )

    // ----------------------- TEST -------------------------
    @JvmStatic
    fun testStart(event: org.gradle.tooling.events.test.TestStartEvent): TestStartEvent =
      TestStartEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        operationDescriptor = testDescriptor(event.descriptor)
      )

    @JvmStatic
    fun testProgress(event: org.gradle.tooling.events.test.TestProgressEvent): TestProgressEvent =
      TestProgressEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        descriptor = testDescriptor(event.descriptor)
      )

    @JvmStatic
    fun testFinish(event: org.gradle.tooling.events.test.TestFinishEvent): TestFinishEvent =
      TestFinishEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        operationDescriptor = testDescriptor(event.descriptor),
        result = testResult(event.result)
      )

    private fun testResult(
      result: org.gradle.tooling.events.test.TestOperationResult
    ): TestOperationResult =
      TestOperationResult(
        startTime = result.startTime,
        endTime = result.endTime,
        success = result is TestSuccessResult
      )

    private fun testDescriptor(
      descriptor: TestOperationDescriptor
    ): com.neo.ide.tooling.events.test.TestOperationDescriptor =
      com.neo.ide.tooling.events.test.TestOperationDescriptor(
        name = descriptor.name,
        displayName = descriptor.displayName
      )

    // ----------------------- TRANSFORM -------------------------
    @JvmStatic
    fun transformStart(
      event: org.gradle.tooling.events.transform.TransformStartEvent
    ): TransformStartEvent =
      TransformStartEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        descriptor = transformDescriptor(event.descriptor)
      )

    @JvmStatic
    fun transformProgress(
      event: org.gradle.tooling.events.transform.TransformProgressEvent
    ): TransformStartEvent =
      TransformStartEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        descriptor = transformDescriptor(event.descriptor)
      )

    @JvmStatic
    fun transformFinish(
      event: org.gradle.tooling.events.transform.TransformFinishEvent
    ): TransformFinishEvent =
      TransformFinishEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        operationDescriptor = transformDescriptor(event.descriptor),
        result = transformResult(event.result)
      )

    private fun transformResult(
      result: org.gradle.tooling.events.transform.TransformOperationResult
    ): TransformOperationResult =
      TransformOperationResult(
        success = result is TransformSuccessResult,
        startTime = result.startTime,
        endTime = result.endTime
      )

    private fun transformDescriptor(
      descriptor: TransformOperationDescriptor
    ): com.neo.ide.tooling.events.transform.TransformOperationDescriptor =
      com.neo.ide.tooling.events.transform.TransformOperationDescriptor(
        name = descriptor.name,
        displayName = descriptor.displayName,
        subject = SubjectDescriptor(descriptor.subject.displayName),
        transformer =
          com.neo.ide.tooling.events.transform.TransformOperationDescriptor
            .TransformerDescriptor(descriptor.transformer.displayName),
        dependencies = descriptor.dependencies.mapNotNull { operationDescriptor(it) }.toSet()
      )

    // ----------------------- WORK ITEM -------------------------
    @JvmStatic
    fun workStart(event: org.gradle.tooling.events.work.WorkItemStartEvent): WorkItemStartEvent =
      WorkItemStartEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        descriptor = workDescriptor(event.descriptor)
      )

    @JvmStatic
    fun workProgress(
      event: org.gradle.tooling.events.work.WorkItemProgressEvent
    ): WorkItemProgressEvent =
      WorkItemProgressEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        descriptor = workDescriptor(event.descriptor)
      )

    @JvmStatic
    fun workFinish(event: org.gradle.tooling.events.work.WorkItemFinishEvent): WorkItemFinishEvent =
      WorkItemFinishEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        operationDescriptor = workDescriptor(event.descriptor),
        result = workResult(event.result)
      )

    private fun workResult(
      result: org.gradle.tooling.events.work.WorkItemOperationResult
    ): WorkItemOperationResult =
      WorkItemOperationResult(
        success = result is WorkItemSuccessResult,
        startTime = result.startTime,
        endTime = result.endTime
      )

    private fun workDescriptor(
      descriptor: WorkItemOperationDescriptor
    ): com.neo.ide.tooling.events.work.WorkItemOperationDescriptor =
      com.neo.ide.tooling.events.work.WorkItemOperationDescriptor(
        name = descriptor.name,
        displayName = descriptor.displayName,
        className = descriptor.className
      )

    // ---------------------------- STATUS ---------------------------------
    fun statusEvent(event: org.gradle.tooling.events.StatusEvent): StatusEvent =
      StatusEvent(
        total = event.total,
        progress = event.progress,
        unit = event.unit,
        displayName = event.displayName,
        eventTime = event.eventTime,
        descriptor = operationDescriptor(event.descriptor)!!
      )

    // ----------------------- DEFAULT ----------------------------------
    fun progress(event: ProgressEvent): com.neo.ide.tooling.events.ProgressEvent =
      DefaultProgressEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        descriptor = operationDescriptor(event.descriptor)!!
      )

    fun start(
      event: org.gradle.tooling.events.StartEvent
    ): com.neo.ide.tooling.events.ProgressEvent =
      DefaultStartEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        descriptor = operationDescriptor(event.descriptor)!!
      )

    fun finish(
      event: org.gradle.tooling.events.FinishEvent
    ): com.neo.ide.tooling.events.ProgressEvent =
      DefaultFinishEvent(
        eventTime = event.eventTime,
        displayName = event.displayName,
        descriptor = operationDescriptor(event.descriptor)!!,
        result = DefaultOperationResult(event.result.startTime, event.result.endTime)
      )
  }
}

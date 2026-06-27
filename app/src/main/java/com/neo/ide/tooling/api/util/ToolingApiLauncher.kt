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

import com.google.gson.GsonBuilder
import com.neo.ide.builder.model.DefaultJavaCompileOptions
import com.neo.ide.builder.model.IJavaCompilerSettings
import com.neo.ide.tooling.api.IProject
import com.neo.ide.tooling.api.IToolingApiClient
import com.neo.ide.tooling.api.IToolingApiServer
import com.neo.ide.tooling.api.models.AndroidProjectMetadata
import com.neo.ide.tooling.api.models.AndroidVariantMetadata
import com.neo.ide.tooling.api.models.BasicAndroidVariantMetadata
import com.neo.ide.tooling.api.models.BasicProjectMetadata
import com.neo.ide.tooling.api.models.GradleTask
import com.neo.ide.tooling.api.models.JavaModuleCompilerSettings
import com.neo.ide.tooling.api.models.JavaModuleDependency
import com.neo.ide.tooling.api.models.JavaModuleExternalDependency
import com.neo.ide.tooling.api.models.JavaModuleProjectDependency
import com.neo.ide.tooling.api.models.JavaProjectMetadata
import com.neo.ide.tooling.api.models.Launchable
import com.neo.ide.tooling.api.models.ProjectMetadata
import com.neo.ide.tooling.events.OperationDescriptor
import com.neo.ide.tooling.events.OperationResult
import com.neo.ide.tooling.events.ProgressEvent
import com.neo.ide.tooling.events.StatusEvent
import com.neo.ide.tooling.events.configuration.ProjectConfigurationFinishEvent
import com.neo.ide.tooling.events.configuration.ProjectConfigurationOperationDescriptor
import com.neo.ide.tooling.events.configuration.ProjectConfigurationOperationResult
import com.neo.ide.tooling.events.configuration.ProjectConfigurationProgressEvent
import com.neo.ide.tooling.events.configuration.ProjectConfigurationStartEvent
import com.neo.ide.tooling.events.download.FileDownloadFinishEvent
import com.neo.ide.tooling.events.download.FileDownloadOperationDescriptor
import com.neo.ide.tooling.events.download.FileDownloadProgressEvent
import com.neo.ide.tooling.events.download.FileDownloadResult
import com.neo.ide.tooling.events.download.FileDownloadStartEvent
import com.neo.ide.tooling.events.internal.DefaultFinishEvent
import com.neo.ide.tooling.events.internal.DefaultOperationDescriptor
import com.neo.ide.tooling.events.internal.DefaultOperationResult
import com.neo.ide.tooling.events.internal.DefaultProgressEvent
import com.neo.ide.tooling.events.internal.DefaultStartEvent
import com.neo.ide.tooling.events.task.TaskExecutionResult
import com.neo.ide.tooling.events.task.TaskFailureResult
import com.neo.ide.tooling.events.task.TaskFinishEvent
import com.neo.ide.tooling.events.task.TaskOperationDescriptor
import com.neo.ide.tooling.events.task.TaskOperationResult
import com.neo.ide.tooling.events.task.TaskProgressEvent
import com.neo.ide.tooling.events.task.TaskSkippedResult
import com.neo.ide.tooling.events.task.TaskStartEvent
import com.neo.ide.tooling.events.task.TaskSuccessResult
import com.neo.ide.tooling.events.test.TestFinishEvent
import com.neo.ide.tooling.events.test.TestOperationDescriptor
import com.neo.ide.tooling.events.test.TestOperationResult
import com.neo.ide.tooling.events.test.TestProgressEvent
import com.neo.ide.tooling.events.test.TestStartEvent
import com.neo.ide.tooling.events.transform.TransformFinishEvent
import com.neo.ide.tooling.events.transform.TransformOperationDescriptor
import com.neo.ide.tooling.events.transform.TransformProgressEvent
import com.neo.ide.tooling.events.transform.TransformStartEvent
import com.neo.ide.tooling.events.work.WorkItemFinishEvent
import com.neo.ide.tooling.events.work.WorkItemOperationDescriptor
import com.neo.ide.tooling.events.work.WorkItemOperationResult
import com.neo.ide.tooling.events.work.WorkItemProgressEvent
import com.neo.ide.tooling.events.work.WorkItemStartEvent
import org.eclipse.lsp4j.jsonrpc.Launcher
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.Executors

/**
 * Utility class for launching [IToolingApiClient] and [IToolingApiServer].
 *
 * @author Akash Yadav
 */
object ToolingApiLauncher {

  fun <T> createIOLauncher(
    local: Any?, remote: Class<T>?, `in`: InputStream?, out: OutputStream?): Launcher<T> {
    return Launcher.Builder<T>()
      .setInput(`in`)
      .setOutput(out)
      .setLocalService(local)
      .setRemoteInterface(remote)
      .configureGson { configureGson(it) }
      .create()
  }

  @JvmStatic
  fun configureGson(builder: GsonBuilder) {
    builder.registerTypeAdapter(File::class.java, FileTypeAdapter())

    // some methods return BasicProjectMetadata while some return ProjectMetadata
    // so we need to register type adapter for both of them
    builder.runtimeTypeAdapter(
      BasicProjectMetadata::class.java,
      ProjectMetadata::class.java,
      AndroidProjectMetadata::class.java,
      JavaProjectMetadata::class.java
    )
    builder.runtimeTypeAdapter(
      ProjectMetadata::class.java,
      AndroidProjectMetadata::class.java,
      JavaProjectMetadata::class.java
    )
    builder.runtimeTypeAdapter(
      BasicAndroidVariantMetadata::class.java,
      AndroidVariantMetadata::class.java
    )
    builder.runtimeTypeAdapter(
      JavaModuleDependency::class.java,
      JavaModuleExternalDependency::class.java,
      JavaModuleProjectDependency::class.java
    )
    builder.runtimeTypeAdapter(
      IJavaCompilerSettings::class.java,
      DefaultJavaCompileOptions::class.java,
      JavaModuleCompilerSettings::class.java
    )
    builder.runtimeTypeAdapter(
      Launchable::class.java,
      GradleTask::class.java
    )
    builder.runtimeTypeAdapter(
      ProgressEvent::class.java,
      ProjectConfigurationProgressEvent::class.java,
      ProjectConfigurationStartEvent::class.java,
      ProjectConfigurationFinishEvent::class.java,

      FileDownloadProgressEvent::class.java,
      FileDownloadStartEvent::class.java,
      FileDownloadFinishEvent::class.java,

      TaskProgressEvent::class.java,
      TaskStartEvent::class.java,
      TaskFinishEvent::class.java,

      TestProgressEvent::class.java,
      TestStartEvent::class.java,
      TestFinishEvent::class.java,

      TransformProgressEvent::class.java,
      TransformStartEvent::class.java,
      TransformFinishEvent::class.java,

      WorkItemProgressEvent::class.java,
      WorkItemStartEvent::class.java,
      WorkItemFinishEvent::class.java,

      DefaultProgressEvent::class.java,
      DefaultStartEvent::class.java,
      DefaultFinishEvent::class.java,

      StatusEvent::class.java
    )
    builder.runtimeTypeAdapter(
      OperationDescriptor::class.java,
      ProjectConfigurationOperationDescriptor::class.java,
      FileDownloadOperationDescriptor::class.java,
      TaskOperationDescriptor::class.java,
      TestOperationDescriptor::class.java,
      TransformOperationDescriptor::class.java,
      WorkItemOperationDescriptor::class.java,
      DefaultOperationDescriptor::class.java
    )
    builder.runtimeTypeAdapter(
      OperationResult::class.java,
      ProjectConfigurationOperationResult::class.java,
      FileDownloadResult::class.java,
      TaskOperationResult::class.java,
      TestOperationResult::class.java,
      WorkItemOperationResult::class.java,
      DefaultOperationResult::class.java
    )
    builder.runtimeTypeAdapter(
      TaskOperationResult::class.java,
      TaskFailureResult::class.java,
      TaskSkippedResult::class.java,
      TaskExecutionResult::class.java,
      TaskSuccessResult::class.java
    )
  }

  private fun <T> GsonBuilder.runtimeTypeAdapter(baseClass: Class<T>,
    vararg subtypes: Class<out T>) {
    registerTypeAdapterFactory(
      RuntimeTypeAdapterFactory.of(baseClass, "gsonType", true)
        .registerSubtype(baseClass, baseClass.name).also { factory ->
          subtypes.forEach { subtype ->
            factory.registerSubtype(subtype, subtype.name)
          }
        }
    )
  }

  fun newClientLauncher(
    client: IToolingApiClient, `in`: InputStream?, out: OutputStream?): Launcher<Any> {
    return newIoLauncher(arrayOf(client), arrayOf(
      IToolingApiServer::class.java, IProject::class.java), `in`, out)
  }

  fun newIoLauncher(
    locals: Array<Any>, remotes: Array<Class<*>?>, `in`: InputStream?,
    out: OutputStream?): Launcher<Any> {
    return Launcher.Builder<Any>()
      .setInput(`in`)
      .setOutput(out)
      .setExecutorService(Executors.newCachedThreadPool())
      .setLocalServices(listOf(*locals))
      .setRemoteInterfaces(listOf(*remotes))
      .configureGson { configureGson(it) }
      .setClassLoader(locals[0].javaClass.classLoader)
      .create()
  }

  @JvmStatic
  fun newServerLauncher(
    server: IToolingApiServer, project: IProject, `in`: InputStream?,
    out: OutputStream?): Launcher<Any> {
    return newIoLauncher(arrayOf(server, project), arrayOf(
      IToolingApiClient::class.java), `in`, out)
  }
}

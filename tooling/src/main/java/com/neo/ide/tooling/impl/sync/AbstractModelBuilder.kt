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

package com.neo.ide.tooling.impl.sync

import com.android.builder.model.v2.ide.SyncIssue
import com.android.builder.model.v2.models.Versions
import com.neo.ide.builder.model.DefaultSyncIssue
import com.neo.ide.builder.model.IDESyncIssue
import com.neo.ide.tooling.api.messages.InitializeProjectParams
import com.neo.ide.tooling.api.util.ToolingProps
import com.neo.ide.utils.AndroidPluginVersion
import com.neo.ide.utils.AndroidPluginVersion.Companion.MINIMUM_SUPPORTED
import com.neo.ide.utils.ILogger
import com.neo.ide.utils.LogUtils
import com.neo.ide.utils.StopWatch
import org.gradle.api.Action
import org.gradle.tooling.BuildController
import org.gradle.tooling.UnknownModelException
import org.gradle.tooling.UnsupportedVersionException
import org.gradle.tooling.model.Model
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Abstract class for [IModelBuilder] implementations.
 *
 * @property androidVariant The name of the variant for which the Android models will be built.
 * @author Akash Yadav
 */
abstract class AbstractModelBuilder<P, R>(
  protected val initializationParams: InitializeProjectParams
) : IModelBuilder<P, R> {

  companion object {

    private val newerAgpWarned = AtomicBoolean(false)

    /**
     * Checks the Android Gradle Plugin version from the given [Versions] model and compares
     * it with [AndroidPluginVersion.MINIMUM_SUPPORTED] and [AndroidPluginVersion.LATEST_TESTED].
     *
     * If the version is less than the [AndroidPluginVersion.MINIMUM_SUPPORTED],
     * throws an [UnsupportedOperationException]. If the version is greater than the
     * [AndroidPluginVersion.LATEST_TESTED], warns the user.
     *
     * @param versions The [Versions] model.
     * @param syncIssueReporter [ISyncIssueReporter] for reporting issues with the Android Gradle Plugin version.
     */
    @JvmStatic
    protected fun checkAgpVersion(
      versions: Versions,
      syncIssueReporter: ISyncIssueReporter
    ) {

      val agpVersion = AndroidPluginVersion.parse(versions.agp)

      // The build should fail if the user is using an older version of AGP
      if (agpVersion < MINIMUM_SUPPORTED) {
        throw ModelBuilderException(
          agpVersion.toString()
              + " is not supported by AndroidIDE. "
              + "Please update your project to use at least "
              + MINIMUM_SUPPORTED
              + " to build this project.")
      }

      // Warn the user if the project is using a newer AGP version
      if (!newerAgpWarned.get() && agpVersion > ToolingProps.latestTestedAgpVersion) {
        val syncIssue = DefaultSyncIssue(
          data = "${agpVersion.toStringSimple()}:${ToolingProps.latestTestedAgpVersion.toStringSimple()}",
          message = "You are using Android Gradle Plugin version that has not been tested with AndroidIDE.",
          multiLineMessage = null,
          severity = SyncIssue.SEVERITY_WARNING,
          type = IDESyncIssue.TYPE_AGP_VERSION_TOO_NEW
        )
        syncIssueReporter.report(syncIssue)
        newerAgpWarned.set(true)
      }
    }

    /**
     * Get the [Versions] information about Android projects. This returns `null` if
     * the project is not an Android project.
     *
     * @param model      The model element, usually a project.
     * @param controller The build controller that is used for finding the model.
     * @return The [Versions] model if available, `null` otherwise.
     */
    @JvmStatic
    protected fun getAndroidVersions(model: Model, controller: BuildController): Versions? {
      return controller.findModel(model, Versions::class.java)
    }

    /**
     * Fetches a snapshot of the model of the given type. Throws a [ModelBuilderException] if the
     * model could not be fetched. This also logs the time consumed to fetch the model.
     *
     * @param modelType The model type.
     * @param <T> The model type.
     */
    @JvmStatic
    protected fun <T> BuildController.getModelAndLog(modelType: Class<T>): T {
      return withStopWatch(modelType) {
        return@withStopWatch try {
          getModel(modelType)
        } catch (err: UnknownModelException) {
          throw ModelBuilderException("Failed to fetch model for type '${modelType.name}'." +
              " Model not found or the project does not support this model.")
        }
      }
    }

    /**
     * Fetches a snapshot of the model of the given type. Throws a [ModelBuilderException] if the
     * model could not be fetched. This also logs the time consumed to fetch the model.
     *
     * @param target The target element, usually a project.
     * @param modelType The model type.
     * @param <T> The model type.
     */
    @JvmStatic
    protected fun <T> BuildController.getModelAndLog(target: Model, modelType: Class<T>): T {
      return withStopWatch(modelType) {
        return@withStopWatch try {
          getModel(target, modelType)
        } catch (err: UnknownModelException) {
          throw ModelBuilderException("Failed to fetch model for type '${modelType.name}'." +
              " Model not found or the project does not support this model.")
        }
      }
    }

    /**
     * Fetches a snapshot of the model of the given type using the given parameter. Throws a
     * [ModelBuilderException] if the model could not be fetched. This also logs the time consumed
     * to fetch the model.
     *
     * @param target The target element, usually a project.
     * @param modelType The model type.
     * @param parameterType The parameter type.
     * @param <P> The parameter type.
     * @param parameterInitializer Action to configure the parameter
     * @param <T> The model type.
     */
    @JvmStatic
    protected fun <P, T> BuildController.getModelAndLog(
      target: Model,
      modelType: Class<T>,
      parameterType: Class<P>,
      parameterInitializer: Action<in P>
    ): T {
      return withStopWatch(modelType) {
        return@withStopWatch try {
          getModel(target, modelType, parameterType, parameterInitializer)
        } catch (err: UnknownModelException) {
          throw ModelBuilderException("Failed to fetch model for type '${modelType.name}'." +
              " Model not found or the project does not support this model.")
        } catch (err: UnsupportedVersionException) {
          throw ModelBuilderException("Failed to fetch model for type '${modelType.name}'." +
              " Model not supported by project or Gradle version does not support parameterized models.")
        }
      }
    }

    @JvmStatic
    private fun <T> withStopWatch(modelType: Class<T>, action: () -> T): T {
      val stopwatch = StopWatch("Fetch '${modelType.simpleName}' model")
      return action().also {
        stopwatch.writeTo(System.err)
      }
    }

    /**
     * Logs the given objects to the error stream.
     *
     * @param objects The objects to log.
     */
    @JvmStatic
    protected fun log(vararg objects: Any?) {
      System.err.println(generateMessage(*objects))
    }

    /**
     * Generates the log message for the given objects. This works similar to
     * [ generateMessage(Object...)][com.neo.ide.utils.ILogger.generateMessage] in [ILogger][com.neo.ide.utils.ILogger].
     *
     * @param objects The objects to print in the message.
     * @return The generated message.
     */
    protected fun generateMessage(vararg objects: Any?): String {
      val sb = StringBuilder()
      for (msg in objects) {
        sb.append(if (msg is Throwable) "\n" else ILogger.MSG_SEPARATOR)
        sb.append(if (msg is Throwable) LogUtils.getFullStackTrace(
          msg as Throwable?) else msg)
      }
      return sb.toString()
    }
  }
}

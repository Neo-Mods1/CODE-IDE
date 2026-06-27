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

package com.neo.ide.tooling.api.messages

import java.io.Serializable

/**
 * Parameters for specifying the Gradle distribution which should be used during project initialization.
 *
 * @property type The type of Gradle distribution.
 * @property value The value for the distribution type.
 * @author Akash Yadav
 */
data class GradleDistributionParams(val type: GradleDistributionType, val value: String) :
  Serializable {

  companion object {

    /**
     * [GradleDistributionParams] with type [GRADLE_WRAPPER][GradleDistributionType.GRADLE_WRAPPER].
     */
    @JvmStatic
    val WRAPPER = GradleDistributionParams(GradleDistributionType.GRADLE_WRAPPER, "")

    /**
     * Creates [GradleDistributionParams] for the given [distribution path][path].
     */
    @JvmStatic
    fun forInstallationDir(path: String): GradleDistributionParams {
      return GradleDistributionParams(GradleDistributionType.GRADLE_INSTALLATION, path)
    }

    /**
     * Creates [GradleDistributionParams] for the given [Gradle version][version].
     */
    @JvmStatic
    fun forVersion(version: String): GradleDistributionParams {
      return GradleDistributionParams(GradleDistributionType.GRADLE_VERSION, version)
    }
  }
}

/**
 * Type of Gradle distributions for project initialization.
 */
enum class GradleDistributionType {

  /**
   * Initialize the project using the distribution specified in `gradle-wrapper.properties`.
   */
  GRADLE_WRAPPER,

  /**
   * Initialize the project using a specific Gradle version.
   */
  GRADLE_VERSION,

  /**
   * Initialize the project using a specific Gradle distribution path.
   */
  GRADLE_INSTALLATION
}

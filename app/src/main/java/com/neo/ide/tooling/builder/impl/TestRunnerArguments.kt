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

package com.android.build.gradle.options

/**
 * A list of standard instrumentation test runner keys.
 *
 * Details: https://developer.android.com/studio/test/command-line#AMOptionsSyntax
 */
enum class TestRunnerArguments(
    private val key: String
) {
    @Suppress("unused")
    PACKAGE("package"),

    @Suppress("unused")
    CLASS("class"),

    @Suppress("unused")
    FUNC("func"),

    @Suppress("unused")
    SIZE("size"),

    @Suppress("unused")
    PERF("perf"),

    @Suppress("unused")
    DEBUG("debug"),

    @Suppress("unused")
    LOG("log"),

    @Suppress("unused")
    EMMA("emma"),

    @Suppress("unused")
    COVERAGE_FILE("coverageFile"),
    ;

    fun getFullKey() : String = "$TEST_RUNNER_ARGS_PREFIX$key"

    fun getShortKey() : String = key

    companion object {
        const val TEST_RUNNER_ARGS_PREFIX = "android.testInstrumentationRunnerArguments."
    }
}

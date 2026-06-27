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

/** An Android Gradle plugin version. */
enum class Version(

    /**
     * String value of the version, or `null` if it is not known (e.g., [VERSION_BEFORE_4_0]).
     *
     * Usage note: Do not use this field to construct a deprecation/removal message, use
     * getDeprecationTargetMessage()/getRemovedVersionMessage() instead to ensure consistent message
     * format.
     */
     val versionString: String?
) {

    /**
     * A version before version 4.0, used when the exact version is not known, except that it's
     * guaranteed to be before 4.0.
     */
    VERSION_BEFORE_4_0(null),

    VERSION_3_5("3.5"),
    VERSION_3_6("3.6"),
    VERSION_4_0("4.0"),
    VERSION_4_1("4.1"),
    VERSION_4_2("4.2"),
    VERSION_7_0("7.0"),
    VERSION_7_2("7.2"),
    VERSION_7_3("7.3"),
    VERSION_8_0("8.0"),
    VERSION_8_1("8.1"),
    VERSION_8_2("8.2"),
    VERSION_8_3("8.3"),
    VERSION_9_0("9.0"),

    ; // end of enums

    fun getDeprecationTargetMessage(): String {
        check(this != VERSION_BEFORE_4_0)
        return "It will be removed in version $versionString of the Android Gradle plugin."
    }

    fun getRemovedVersionMessage(): String {
        return if (this == VERSION_BEFORE_4_0) {
            "It has been removed from the current version of the Android Gradle plugin."
        } else {
            "It was removed in version $versionString of the Android Gradle plugin."
        }
    }
}

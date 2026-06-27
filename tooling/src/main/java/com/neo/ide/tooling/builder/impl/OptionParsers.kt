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

@file:JvmName("OptionParsers")

package com.android.build.gradle.options

import java.util.Locale

fun parseBoolean(propertyName: String, value: Any, propertyKind: String = "project"): Boolean {
    return when (value) {
        is Boolean -> value
        is CharSequence ->
            when (value.toString().lowercase(Locale.US)) {
                "true" -> true
                "false" -> false
                else -> parseBooleanFailure(propertyName, value, propertyKind)
            }
        is Number ->
            when (value.toInt()) {
                0 -> false
                1 -> true
                else -> parseBooleanFailure(propertyName, value, propertyKind)
            }
        else -> parseBooleanFailure(propertyName, value, propertyKind)
    }
}

private fun parseBooleanFailure(propertyName: String, value: Any, propertyKind: String): Nothing {
    throw IllegalArgumentException(
        "Cannot parse "
                + propertyKind
                + " property "
                + propertyName
                + "='"
                + value
                + "' of type '"
                + value.javaClass
                + "' as boolean. Expected 'true' or 'false'."
    )
}

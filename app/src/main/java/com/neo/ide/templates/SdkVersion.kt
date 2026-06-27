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

package com.neo.ide.templates

enum class SdkVersion(val api: Int, val version: String, val codename: String) {
    API_21(21, "5.0", "Lollipop"),
    API_22(22, "5.1", "Lollipop"),
    API_23(23, "6.0", "Marshmallow"),
    API_24(24, "7.0", "Nougat"),
    API_25(25, "7.1", "Nougat"),
    API_26(26, "8.0", "Oreo"),
    API_27(27, "8.1", "Oreo"),
    API_28(28, "9.0", "Pie"),
    API_29(29, "10", "Q"),
    API_30(30, "11", "R"),
    API_31(31, "12", "Snow Cone"),
    API_32(32, "12L", "Snow Cone"),
    API_33(33, "13", "Tiramisu"),
    API_34(34, "14", "UpsideDownCake"),
    API_35(35, "15", "Vanilla Ice Cream"),
    API_36(36, "16", "Baklava");

    fun displayName(): String = "API $api: Android $version ($codename)"

    companion object {
        fun fromApi(api: Int): SdkVersion = entries.find { it.api == api } ?: API_34
    }
}

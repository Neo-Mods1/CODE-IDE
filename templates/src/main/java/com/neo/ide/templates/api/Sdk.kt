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

/*
 *  This file is part of AndroidIDE.
 *
 *  AndroidIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  AndroidIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with AndroidIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.neo.ide.templates.api

/**
 * Android API versions.
 *
 * @author Akash Yadav
 */
enum class Sdk(val codename: String, val version: String, val api: Int) {
  JellyBean("Jelly Bean", "4.1", 16),
  JellyBeanMR1("Jelly Bean", "4.2", 17),
  JellyBeanMR2("Jelly Bean", "4.3", 18),
  KitKat("KitKat", "4.4", 19),
  KitKatWatch("KitKat Watch", "4.4W", 20),
  Lollipop("Lollipop", "5.0", 21),
  LollipopMR1("Lollipop", "5.1", 22),
  Marshmallow("Marshmallow", "6.0", 23),
  Naughat("Naughat", "7.0", 24),
  NaughtMR1("Naughat", "7.1", 25),
  Oreo("Oreo", "8.0", 26),
  OreoMR1("Oreo", "8.1", 27),
  Pie("Pie", "9.0", 28),
  QuinceTart("Q", "10", 29),
  RedVelvetCake("R", "11", 30),
  SnowCone("SnowCone", "12", 31),
  SnowCodeV2("SnowCone", "12L", 32),
  Tiramisu("Tiramisu", "13", 33),
  UpsideDownCake("UpsideDownCake", "14", 34);

  /**
   * Get the display name for this Sdk version.
   *
   * @return The display name.
   */
  fun displayName() : String = "API ${api}: Android $version (${codename})"
}
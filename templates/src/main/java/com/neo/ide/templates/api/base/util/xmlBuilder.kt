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

package com.neo.ide.templates.api.base.util

import org.eclipse.lemminx.dom.builder.IndentedXmlBuilder
import org.eclipse.lemminx.dom.builder.XmlBuilder

/**
 * Creates a new element in the [XmlBuilder] with the given [name].
 * The [configure] function is used to configure everything inside the element and then
 * the element is automatically closed.
 */
inline fun XmlBuilder.createElement(name: String, closeStartTag: Boolean = false, selfClose: Boolean = false, crossinline configure: XmlBuilder.() -> Unit) {
  startElement(name, closeStartTag)
  configure()
  if (selfClose) {
    selfCloseElement()
  } else {
    endElement(name)
  }
}

/**
 * Adds a new `<string>` resource entry to this [XmlBuilder].
 *
 * @param name The name of the string resource.
 * @param value The value of the XML resource.
 */
fun XmlBuilder.stringRes(name: String, value: String, indent: Boolean = true) {
  indent(1)
  createElement("string") {
    addSingleAttribute("name", name)
    closeStartElement()
    append(value)
  }
  linefeed()
}

/**
 * Get the generated XML string from the [XmlBuilder] with XML declaration prepended.
 */
fun XmlBuilder.withXmlDecl(): String {
  return toString().withXmlDecl()
}

/**
 * Prepends XML declaration to this string.
 */
fun String.withXmlDecl(): String {
  return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n${this}"
}
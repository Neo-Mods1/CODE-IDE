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

import com.neo.ide.utils.ServiceLoader

/**
 * An [ITemplateProvider] provides templates to the IDE.
 *
 * @author Akash Yadav
 */
interface ITemplateProvider {

  companion object {

    private var provider: ITemplateProvider? = null

    /**
     * Get the template provider instance.
     *
     * @param reload Whether to reload the provider. If the value is `true`
     * and the provider is cached, the provider is cleared and loaded again.
     */
    @JvmStatic
    @JvmOverloads
    fun getInstance(reload: Boolean = false): ITemplateProvider {

      return provider?.also { if (reload) it.reload() } ?: ServiceLoader.load(
        ITemplateProvider::class.java)
        .findFirstOrThrow()
        .also { provider = it }
    }

    /**
     * @return Whether the [ITemplateProvider] has been loaded or not.
     */
    @JvmStatic
    fun isLoaded() = provider != null
  }

  /**
   * Get the templates.
   *
   * @return The templates.
   */
  fun getTemplates(): List<Template<*>>

  /**
   * Get the template with the given id.
   *
   * @param templateId The ID for the template.
   * @return The [Template] with the given [templateId] if any, or `null`.
   */
  fun getTemplate(templateId: String): Template<*>?

  /**
   * Reloads the templates.
   */
  fun reload()

  /**
   * Clear all the templates stored in the provider.
   */
  fun release()
}
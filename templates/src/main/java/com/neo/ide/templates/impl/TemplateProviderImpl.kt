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

package com.neo.ide.templates.impl
import com.google.auto.service.AutoService
import com.google.common.collect.ImmutableList
import com.neo.ide.templates.api.ITemplateProvider
import com.neo.ide.templates.api.Template
import com.neo.ide.templates.impl.basicActivity.basicActivityProject
import com.neo.ide.templates.impl.bottomNavActivity.bottomNavActivityProject
import com.neo.ide.templates.impl.composeActivity.composeActivityProject
import com.neo.ide.templates.impl.emptyActivity.emptyActivityProject
import com.neo.ide.templates.impl.navDrawerActivity.navDrawerActivityProject
import com.neo.ide.templates.impl.noActivity.noActivityProjectTemplate
import com.neo.ide.templates.impl.noAndroidXActivity.noAndroidXActivityProject
import com.neo.ide.templates.impl.tabbedActivity.tabbedActivityProject
@Suppress("unused")
@AutoService(ITemplateProvider::class)
class TemplateProviderImpl : ITemplateProvider {
  private val templates = mutableMapOf<String, Template<*>>()
  init {
    initializeTemplates()
  }
  private fun templates() =
    //@formatter:off
    arrayOf(
      noActivityProjectTemplate(),
      emptyActivityProject(),
      basicActivityProject(),
      navDrawerActivityProject(),
      bottomNavActivityProject(),
      tabbedActivityProject(),
      noAndroidXActivityProject(),
      composeActivityProject()
    )
  private fun initializeTemplates() {
    templates().forEach { template ->
      templates[template.templateId] = template
    }
  }
  //@formatter:on
  override fun getTemplates(): List<Template<*>> {
    return ImmutableList.copyOf(templates.values)
  }
  override fun getTemplate(templateId: String): Template<*>? {
    return templates[templateId]
  }
  override fun reload() {
    release()
    initializeTemplates()
  }
  override fun release() {
    templates.forEach { it.value.release() }
    templates.clear()
  }
}

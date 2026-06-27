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



package com.neo.ide.lsp.util

import com.neo.ide.lookup.Lookup
import com.neo.ide.projects.IProjectManager
import com.neo.ide.projects.android.AndroidModule
import com.neo.ide.projects.ModuleProject
import com.neo.ide.xml.resources.ResourceTableRegistry
import com.neo.ide.xml.versions.ApiVersions
import com.neo.ide.xml.widgets.WidgetTable
import java.io.File
import java.nio.file.Path

fun setupLookupForCompletion(file: File) {
  setupLookupForCompletion(file.toPath())
}

fun setupLookupForCompletion(file: Path) {
  val module =
    IProjectManager.getInstance().getWorkspace()?.findModuleForFile(file, false) ?: return
  val lookup = Lookup.getDefault()

  lookup.update(ModuleProject.COMPLETION_MODULE_KEY, module)

  if (module is AndroidModule) {
    val versions = module.getApiVersions()
    if (versions != null) {
      lookup.update(ApiVersions.COMPLETION_LOOKUP_KEY, versions)
    }

    val widgets = module.getWidgetTable()
    if (widgets != null) {
      lookup.update(WidgetTable.COMPLETION_LOOKUP_KEY, widgets)
    }

    val frameworkResources = module.getFrameworkResourceTable()
    if (frameworkResources != null) {
      lookup.update(ResourceTableRegistry.COMPLETION_FRAMEWORK_RES, frameworkResources)
    }

    val moduleResources = module.getSourceResourceTables()
    if (moduleResources.isNotEmpty()) {
      lookup.update(ResourceTableRegistry.COMPLETION_MODULE_RES, moduleResources)
    }

    val depResTables = module.getDependencyResourceTables()
    if (depResTables.isNotEmpty()) {
      lookup.update(ResourceTableRegistry.COMPLETION_DEP_RES, depResTables)
    }

    val manifestAttrTable = module.getManifestAttrTable()
    if (manifestAttrTable != null) {
      lookup.update(ResourceTableRegistry.COMPLETION_MANIFEST_ATTR_RES, manifestAttrTable)
    }
  }
}
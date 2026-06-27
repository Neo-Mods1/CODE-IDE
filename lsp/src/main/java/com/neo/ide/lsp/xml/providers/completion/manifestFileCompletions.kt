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



package com.neo.ide.lsp.xml.providers.completion

import com.android.SdkConstants.ANDROID_MANIFEST_XML
import com.android.aaptcompiler.ResourcePathData
import com.neo.ide.lookup.Lookup
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType
import com.neo.ide.utils.VMUtils
import com.neo.ide.xml.res.IResourceTable
import com.neo.ide.xml.resources.ResourceTableRegistry

const val MANIFEST_TAG_PREFIX = "AndroidManifest"

fun canCompleteManifest(pathData: ResourcePathData, type: NodeType): Boolean {
  return pathData.file.name == ANDROID_MANIFEST_XML ||
    (VMUtils.isJvm() &&
      pathData.file.name.startsWith("Manifest") &&
      pathData.file.name.endsWith("_template.xml"))
}

fun manifestResourceTable(): Set<IResourceTable> {
  return setOf(
    Lookup.getDefault().lookup(ResourceTableRegistry.COMPLETION_MANIFEST_ATTR_RES)
      ?: return emptySet()
  )
}
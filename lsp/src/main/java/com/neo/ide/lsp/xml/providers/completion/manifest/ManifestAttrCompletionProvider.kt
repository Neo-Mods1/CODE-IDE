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



package com.neo.ide.lsp.xml.providers.completion.manifest

import com.android.aaptcompiler.ConfigDescription
import com.android.aaptcompiler.ResourcePathData
import com.android.aaptcompiler.Styleable
import com.neo.ide.lsp.api.ICompletionProvider
import com.neo.ide.lsp.xml.providers.completion.AttrCompletionProvider
import com.neo.ide.lsp.xml.providers.completion.MANIFEST_TAG_PREFIX
import com.neo.ide.lsp.xml.providers.completion.canCompleteManifest
import com.neo.ide.lsp.xml.providers.completion.manifestResourceTable
import com.neo.ide.lsp.xml.providers.completion.transformToEntryName
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType
import com.neo.ide.xml.res.IResourceGroup
import org.eclipse.lemminx.dom.DOMNode

/**
 * Provides attribution completion for AndroidManifest.
 *
 * @author Akash Yadav
 */
class ManifestAttrCompletionProvider(provider: ICompletionProvider) :
  AttrCompletionProvider(provider) {

  override fun canProvideCompletions(pathData: ResourcePathData, type: NodeType): Boolean {
    return super.canProvideCompletions(pathData, type) && canCompleteManifest(pathData, type)
  }

  override fun findResourceTables(nsUri: String?) = manifestResourceTable()

  override fun findNodeStyleables(node: DOMNode, styleables: IResourceGroup): Set<Styleable> {
    val name = node.nodeName
    val styleable =
      styleables.findEntry(transformToEntryName(name, MANIFEST_TAG_PREFIX))
        ?.findValue(ConfigDescription())?.value
    if (styleable != null && styleable is Styleable) {
      return setOf(styleable)
    }

    return emptySet()
  }
}

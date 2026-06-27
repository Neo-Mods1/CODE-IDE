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



package com.neo.ide.lsp.xml.providers.completion.etc

import com.android.aaptcompiler.Styleable
import com.neo.ide.lsp.api.ICompletionProvider
import com.neo.ide.lsp.xml.providers.completion.AttrCompletionProvider
import com.neo.ide.lsp.xml.utils.ITagTransformer
import com.neo.ide.xml.res.IResourceGroup
import org.eclipse.lemminx.dom.DOMNode

/**
 * Provides attribute completion for entries which inherit attributes from other entries.
 *
 * @author Akash Yadav
 */
class InheritingAttrCompletionProvider(
  private val parentProvider: (String) -> List<String> = { emptyList() },
  private val tagTransform: ITagTransformer,
  provider: ICompletionProvider
) : AttrCompletionProvider(provider) {

  override fun findNodeStyleables(node: DOMNode, styleables: IResourceGroup): Set<Styleable> {
    val nodeStyleables = mutableSetOf<Styleable>()
    val name = node.nodeName
    val entryName = tagTransform.transform(name, nodeAtCursor.parentNode?.nodeName ?: "")
    val styleable = findStyleableEntry(styleables, entryName)
    styleable?.let { nodeStyleables.add(it) }

    val parents = parentProvider(entryName)
    if (parents.isNotEmpty()) {
      parents.forEach {
        findStyleableEntry(styleables, it)?.let { parentStyleable ->
          nodeStyleables.add(parentStyleable)
        }
      }
    }
    return nodeStyleables
  }
}

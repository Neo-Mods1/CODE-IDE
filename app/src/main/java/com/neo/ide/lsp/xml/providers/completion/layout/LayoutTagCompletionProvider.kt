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



package com.neo.ide.lsp.xml.providers.completion.layout

import com.android.aaptcompiler.ResourcePathData
import com.neo.ide.lsp.api.ICompletionProvider
import com.neo.ide.lsp.models.CompletionParams
import com.neo.ide.lsp.models.CompletionResult
import com.neo.ide.lsp.xml.providers.completion.IXmlCompletionProvider
import com.neo.ide.lsp.xml.providers.completion.canCompleteLayout
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType.TAG
import org.eclipse.lemminx.dom.DOMDocument

/**
 * [LayoutCompletionProvider] implementation for providing completing tags in an XML layout file.
 *
 * @author Akash Yadav
 */
open class LayoutTagCompletionProvider(val provider: ICompletionProvider) :
  IXmlCompletionProvider(provider) {

  override fun canProvideCompletions(pathData: ResourcePathData, type: NodeType): Boolean {
    return super.canProvideCompletions(pathData, type) && canCompleteLayout(pathData, type) && type == TAG
  }

  override fun doComplete(
    params: CompletionParams,
    pathData: ResourcePathData,
    document: DOMDocument,
    type: NodeType,
    prefix: String
  ): CompletionResult {
    val newPrefix =
      if (prefix.startsWith("<")) {
        prefix.substring(1)
      } else {
        prefix
      }

    return getCompleter(newPrefix).complete(params, pathData, document, type, newPrefix)
  }

  private fun getCompleter(prefix: String): IXmlCompletionProvider {
    if (prefix.contains('.')) {
      return QualifiedTagCompleter(provider)
    }

    return SimpleTagCompleter(provider)
  }
}

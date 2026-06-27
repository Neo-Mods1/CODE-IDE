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
import com.neo.ide.lookup.Lookup
import com.neo.ide.lsp.api.ICompletionProvider
import com.neo.ide.lsp.models.CompletionItem
import com.neo.ide.lsp.models.CompletionParams
import com.neo.ide.lsp.models.CompletionResult
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType
import com.neo.ide.projects.ModuleProject
import com.neo.ide.utils.ClassTrie
import com.neo.ide.xml.widgets.WidgetTable
import org.eclipse.lemminx.dom.DOMDocument

/**
 * Completes platform widget names.
 *
 * @author Akash Yadav
 */
class SimpleTagCompleter(provider: ICompletionProvider) : LayoutTagCompletionProvider(provider) {

  override fun doComplete(
    params: CompletionParams,
    pathData: ResourcePathData,
    document: DOMDocument,
    type: NodeType,
    prefix: String
  ): CompletionResult {
    val widgets =
      Lookup.getDefault().lookup(WidgetTable.COMPLETION_LOOKUP_KEY)?.getAllWidgets()
        ?: return CompletionResult.EMPTY
    val result = mutableListOf<CompletionItem>()

    // Complete all tags which do not require fully qualified name
    for (widget in widgets) {
      val match = matchLevel(widget.simpleName, prefix)
      result.add(createTagCompletionItem(widget.simpleName, widget.qualifiedName, match, true))
    }

    // Complete the root package names if possible
    val module =
      Lookup.getDefault().lookup(ModuleProject.COMPLETION_MODULE_KEY) ?: return CompletionResult(result)

    // Add root packages from the compile classpath and source paths
    addFromTrie(module.compileClasspathClasses, prefix, result)
    addFromTrie(module.compileJavaSourceClasses, prefix, result)

    return CompletionResult(result)
  }

  private fun addFromTrie(trie: ClassTrie, prefix: String, result: MutableList<CompletionItem>) {
    trie.root.children.values.forEach {
      val match = matchLevel(it.name, prefix)
      result.add(createTagCompletionItem(it.name, it.qualifiedName, match))
    }
  }
}

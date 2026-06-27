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
import com.neo.ide.lsp.models.MatchLevel.NO_MATCH
import com.neo.ide.lsp.xml.providers.completion.match
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType
import com.neo.ide.projects.ModuleProject
import com.neo.ide.utils.ClassTrie
import com.neo.ide.xml.internal.widgets.DefaultWidgetTable
import com.neo.ide.xml.widgets.WidgetTable
import org.eclipse.lemminx.dom.DOMDocument

/**
 * Completes tags from
 *
 * @author Akash Yadav
 */
class QualifiedTagCompleter(provider: ICompletionProvider) : LayoutTagCompletionProvider(provider) {

  override fun doComplete(
    params: CompletionParams,
    pathData: ResourcePathData,
    document: DOMDocument,
    type: NodeType,
    prefix: String
  ): CompletionResult {
    val result = mutableListOf<CompletionItem>()
    val (widgets, module) = doLookup()
    var fqn = prefix
    if (prefix.endsWith('.')) {
      fqn = fqn.substringBeforeLast('.')
    }

    widgets.getNode(name = fqn, createIfNotPresent = false)?.children?.values?.forEach {
      val qualifiedName = "$fqn.${it.name}"
      val match = match(it.name, qualifiedName, prefix)
      result.add(createTagCompletionItem(it.name, qualifiedName, match))
    }

    addFromTrie(module.compileClasspathClasses, fqn, prefix, result)
    addFromTrie(module.compileJavaSourceClasses, fqn, prefix, result)

    return CompletionResult(result)
  }

  private fun addFromTrie(
    trie: ClassTrie,
    fqn: String,
    prefix: String,
    result: MutableList<CompletionItem>
  ) {
    val node = trie.findNode(fqn) ?: trie.findNode(fqn.substringBeforeLast('.')) ?: return
    node.children.values.forEach {
      val match = match(it.name, it.qualifiedName, prefix)
      if (match == NO_MATCH) {
        return@forEach
      }
      
      result.add(createTagCompletionItem(it.name, it.qualifiedName, match))
    }
  }

  private fun doLookup(): Pair<DefaultWidgetTable, ModuleProject> {
    val widgets =
      Lookup.getDefault().lookup(WidgetTable.COMPLETION_LOOKUP_KEY)
        ?: throw IllegalStateException("No widget table provided")
    val module =
      Lookup.getDefault().lookup(ModuleProject.COMPLETION_MODULE_KEY)
        ?: throw IllegalStateException("No module project provided")
    return widgets as DefaultWidgetTable to module
  }
}

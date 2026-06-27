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

import com.android.SdkConstants
import com.android.SdkConstants.ANDROID_NS_NAME_PREFIX
import com.android.SdkConstants.ATTR_NAME
import com.android.SdkConstants.TAG_ACTIVITY
import com.android.SdkConstants.TAG_INTENT_FILTER
import com.android.SdkConstants.TAG_RECEIVER
import com.android.SdkConstants.TAG_SERVICE
import com.android.aaptcompiler.ResourcePathData
import com.neo.ide.lookup.Lookup
import com.neo.ide.lsp.api.ICompletionProvider
import com.neo.ide.lsp.models.ClassCompletionData
import com.neo.ide.lsp.models.CompletionItem
import com.neo.ide.lsp.models.CompletionItemKind.FIELD
import com.neo.ide.lsp.models.CompletionParams
import com.neo.ide.lsp.models.CompletionResult
import com.neo.ide.lsp.models.CompletionResult.Companion.EMPTY
import com.neo.ide.lsp.models.FieldCompletionData
import com.neo.ide.lsp.models.InsertTextFormat.PLAIN_TEXT
import com.neo.ide.lsp.models.MatchLevel.NO_MATCH
import com.neo.ide.lsp.xml.providers.completion.AttrValueCompletionProvider
import com.neo.ide.lsp.xml.providers.completion.manifestResourceTable
import com.neo.ide.lsp.xml.providers.completion.match
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType
import com.neo.ide.projects.ModuleProject
import com.neo.ide.projects.android.AndroidModule
import com.neo.ide.xml.permissions.Permission
import com.neo.ide.xml.res.IResourceTable
import com.neo.ide.xml.resources.ResourceTableRegistry
import org.eclipse.lemminx.dom.DOMDocument

/**
 * Completes attribute values in manifest file.
 *
 * @author Akash Yadav
 */
class ManifestAttrValueCompletionProvider(provider: ICompletionProvider) :
  AttrValueCompletionProvider(provider) {

  override fun doComplete(
    params: CompletionParams,
    pathData: ResourcePathData,
    document: DOMDocument,
    type: NodeType,
    prefix: String
  ): CompletionResult {
    if (this.attrAtCursor.nodeName == /*android:name*/ "${ANDROID_NS_NAME_PREFIX}${ATTR_NAME}") {
      return when (this.nodeAtCursor.nodeName) {
        "action" -> completeActionName(prefix)
        "category" -> completeCategory(prefix)
        "uses-permission" -> completePermission(prefix)
        "uses-feature" -> completeFeature(prefix)
        else -> return super.doComplete(params, pathData, document, type, prefix)
      }
    }
    return super.doComplete(params, pathData, document, type, prefix)
  }

  override fun resTableForFindAttr(): IResourceTable? {
    return manifestResourceTable().firstOrNull()
  }

  // TODO we could add an action using the actions registry to make it easier to add permissions
  //  with a GUI interface
  private fun completePermission(prefix: String): CompletionResult {
    val result = mutableListOf<CompletionItem>()
    for (value in Permission.values()) {
      val match = match(value.name, value.constant, prefix)
      if (match == NO_MATCH) {
        continue
      }

      val item =
        createEnumOrFlagCompletionItem(ResourceTableRegistry.PCK_ANDROID, value.name, match)
      item.insertText = value.constant
      item.insertTextFormat = PLAIN_TEXT
      item.overrideTypeText = "Permission"

      // Show API information
      item.completionKind = FIELD
      item.data =
        FieldCompletionData(
          memberName = value.name,
          classInfo = ClassCompletionData(className = SdkConstants.CLASS_MANIFEST_PERMISSION)
        )
      result.add(item)
    }
    return CompletionResult(result)
  }

  private fun completeActionName(prefix: String): CompletionResult {
    val parent = this.nodeAtCursor.parentNode ?: return EMPTY
    val parentOfParent = parent.parentNode ?: return EMPTY

    val result = mutableListOf<CompletionItem>()
    if (parent.nodeName == TAG_INTENT_FILTER) {
      when (parentOfParent.nodeName) {
        TAG_ACTIVITY -> completeActivityActions(prefix, result)
        TAG_RECEIVER -> completeReceiverActions(prefix, result)
        TAG_SERVICE -> completeServiceActions(prefix, result)
      }
    }

    return CompletionResult(result)
  }

  private fun completeServiceActions(prefix: String, result: MutableList<CompletionItem>) {
    val module = getModule()
    if (module is AndroidModule) {
      addMatches(prefix, module.getServiceActions(), result)
    }
  }

  private fun completeActivityActions(prefix: String, result: MutableList<CompletionItem>) {
    val module = getModule()
    if (module is AndroidModule) {
      addMatches(prefix, module.getActivityActions(), result)
    }
  }

  private fun completeReceiverActions(prefix: String, result: MutableList<CompletionItem>) {
    val module = getModule()
    if (module is AndroidModule) {
      addMatches(prefix, module.getBroadcastActions(), result)
    }
  }

  private fun completeCategory(prefix: String): CompletionResult {
    val result = mutableListOf<CompletionItem>()
    val mod = getModule()
    if (mod is AndroidModule) {
      addMatches(prefix, mod.getCategories(), result)
    }
    return CompletionResult(result)
  }

  private fun completeFeature(prefix: String): CompletionResult {
    val result = mutableListOf<CompletionItem>()
    val mod = getModule()
    if (mod is AndroidModule) {
      addMatches(prefix, mod.getFeatures(), result)
    }
    return CompletionResult(result)
  }

  private fun addMatches(
    prefix: String,
    entries: List<String>,
    result: MutableList<CompletionItem>
  ) {
    for (entry in entries) {
      val match =
        if (entry.contains('.')) {
          match(entry.substringAfterLast('.'), entry, prefix)
        } else {
          matchLevel(entry, prefix)
        }

      if (match == NO_MATCH) {
        continue
      }

      val item = createEnumOrFlagCompletionItem(ResourceTableRegistry.PCK_ANDROID, entry, match)
      item.insertText = entry
      item.insertTextFormat = PLAIN_TEXT
      result.add(item)
    }
  }

  private fun getModule(): ModuleProject {
    return Lookup.getDefault().lookup(ModuleProject.COMPLETION_MODULE_KEY)
      ?: throw IllegalStateException("No module project provided")
  }

  override fun findResourceTables(nsUri: String?): Set<IResourceTable> {
    val tables = manifestResourceTable().toMutableSet()
    if (nsUri.isNullOrBlank()) {
      return tables
    }
    tables.addAll(super.findResourceTables(nsUri))
    log.info("Found ${tables.size} resource tables for namespace: $nsUri")
    return tables
  }
}

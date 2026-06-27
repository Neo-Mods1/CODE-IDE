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



package com.neo.ide.lsp.xml.providers

import com.android.SdkConstants.ANDROID_MANIFEST_XML
import com.android.aaptcompiler.AaptResourceType.ANIM
import com.android.aaptcompiler.AaptResourceType.ANIMATOR
import com.android.aaptcompiler.AaptResourceType.DRAWABLE
import com.android.aaptcompiler.AaptResourceType.LAYOUT
import com.android.aaptcompiler.AaptResourceType.MENU
import com.android.aaptcompiler.AaptResourceType.TRANSITION
import com.android.aaptcompiler.ResourcePathData
import com.android.aaptcompiler.extractPathData
import com.neo.ide.lsp.api.AbstractServiceProvider
import com.neo.ide.lsp.api.ICompletionProvider
import com.neo.ide.lsp.api.IServerSettings
import com.neo.ide.lsp.models.CompletionParams
import com.neo.ide.lsp.models.CompletionResult
import com.neo.ide.lsp.models.CompletionResult.Companion.EMPTY
import com.neo.ide.lsp.xml.providers.completion.AttrValueCompletionProvider
import com.neo.ide.lsp.xml.providers.completion.IXmlCompletionProvider
import com.neo.ide.lsp.xml.providers.completion.canCompleteManifest
import com.neo.ide.lsp.xml.providers.completion.common.CommonAttrCompletionProvider
import com.neo.ide.lsp.xml.providers.completion.etc.InheritingAttrCompletionProvider
import com.neo.ide.lsp.xml.providers.completion.layout.LayoutAttrCompletionProvider
import com.neo.ide.lsp.xml.providers.completion.layout.LayoutTagCompletionProvider
import com.neo.ide.lsp.xml.providers.completion.manifest.ManifestAttrCompletionProvider
import com.neo.ide.lsp.xml.providers.completion.manifest.ManifestAttrValueCompletionProvider
import com.neo.ide.lsp.xml.providers.completion.manifest.ManifestTagCompletionProvider
import com.neo.ide.lsp.xml.utils.AnimTagTransformer
import com.neo.ide.lsp.xml.utils.AnimatorTagTransformer
import com.neo.ide.lsp.xml.utils.DrawableTagTransformer
import com.neo.ide.lsp.xml.utils.ITagTransformer
import com.neo.ide.lsp.xml.utils.MenuTagTransformer
import com.neo.ide.lsp.xml.utils.NoOpTagTransformer
import com.neo.ide.lsp.xml.utils.TransitionTagTransformer
import com.neo.ide.lsp.xml.utils.XmlUtils
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType.ATTRIBUTE
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType.ATTRIBUTE_VALUE
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType.TAG
import com.neo.ide.lsp.xml.utils.XmlUtils.NodeType.UNKNOWN
import com.neo.ide.lsp.xml.utils.forTransitionAttr
import com.neo.ide.utils.CharSequenceReader
import com.neo.ide.utils.StopWatch
import io.github.rosemoe.sora.text.ContentReference
import org.eclipse.lemminx.dom.DOMParser
import org.eclipse.lemminx.uriresolver.URIResolverExtensionManager
import org.slf4j.LoggerFactory
import java.io.Reader
import kotlin.io.path.name

/**
 * Completion provider for XML files.
 *
 * @author Akash Yadav
 */
class XmlCompletionProvider(settings: IServerSettings) :
  AbstractServiceProvider(), ICompletionProvider {

  companion object {

    private val log = LoggerFactory.getLogger(XmlCompletionProvider::class.java)
  }

  init {
    super.applySettings(settings)
  }

  override fun complete(params: CompletionParams): CompletionResult {
    return try {
      val watch =
        StopWatch(
          "Complete at ${params.file.name}:${params.position.line}:${params.position.column}"
        )
      doComplete(params).also { watch.log() }
    } catch (error: Throwable) {
      log.error("An error occurred while computing XML completions", error)
      EMPTY
    }
  }

  private fun doComplete(params: CompletionParams): CompletionResult {
    val contents = toString(contents = params.requireContents())
    val document =
      DOMParser.getInstance().parse(contents, "http://schemas.android.com/apk/res/android",
        URIResolverExtensionManager())
    val type = XmlUtils.getNodeType(document, params.position.requireIndex())

    if (type == UNKNOWN) {
      log.warn("Unknown node type. Aborting completion.")
      return EMPTY
    }

    val prefix = XmlUtils.getPrefix(document, params.position.requireIndex(), type) ?: return EMPTY
    if (prefix.isBlank() && type != ATTRIBUTE_VALUE) {
      return EMPTY
    }

    val pathData = extractPathData(params.file.toFile())

    val completer =
      getCompleter(pathData, type)
        ?: run {
          log.error(
            "No completer available for resource type '{}' and node type '{}'", pathData.type, type
          )
          return EMPTY
        }

    return completer.complete(params, pathData, document, type, prefix)
  }

  private fun toString(contents: CharSequence): String {
    return getReader(contents).use { it.readText() }
  }

  private fun getReader(contents: CharSequence): Reader =
    if (contents is ContentReference) {
      contents.createReader()
    } else {
      CharSequenceReader(contents)
    }

  private fun getCompleter(pathData: ResourcePathData, type: NodeType): IXmlCompletionProvider? {
    return when (pathData.type) {
      LAYOUT -> createLayoutCompleter(type)
      TRANSITION -> createTransitionCompleter(type)
      null -> createNullTypeCompleter(pathData, type)
      else -> createCommonCompleter(pathData, type)
    }
  }

  private fun createTransitionCompleter(type: NodeType): IXmlCompletionProvider? {
    return when (type) {
      ATTRIBUTE ->
        InheritingAttrCompletionProvider(::forTransitionAttr, TransitionTagTransformer, this)

      ATTRIBUTE_VALUE -> AttrValueCompletionProvider(this)
      else -> null
    }
  }

  private fun createCommonCompleter(
    pathData: ResourcePathData,
    type: NodeType
  ): IXmlCompletionProvider? {
    return when (type) {
      ATTRIBUTE -> CommonAttrCompletionProvider(tagTransformerFor(pathData), this)
      ATTRIBUTE_VALUE -> AttrValueCompletionProvider(this)
      else -> null
    }
  }

  private fun tagTransformerFor(pathData: ResourcePathData): ITagTransformer {
    return when (pathData.type) {
      ANIM -> AnimTagTransformer
      ANIMATOR -> AnimatorTagTransformer
      DRAWABLE -> DrawableTagTransformer
      MENU -> MenuTagTransformer
      else -> NoOpTagTransformer
    }
  }

  private fun createNullTypeCompleter(
    pathData: ResourcePathData,
    type: NodeType
  ): IXmlCompletionProvider? {

    // In test cases
    if (canCompleteManifest(pathData, type)) {
      return createManifestCompleter(type)
    }

    return when (pathData.file.name) {
      ANDROID_MANIFEST_XML -> createManifestCompleter(type)
      else -> null
    }
  }

  private fun createManifestCompleter(type: NodeType): IXmlCompletionProvider? {
    return when (type) {
      TAG -> ManifestTagCompletionProvider(this)
      ATTRIBUTE -> ManifestAttrCompletionProvider(this)
      ATTRIBUTE_VALUE -> ManifestAttrValueCompletionProvider(this)
      else -> null
    }
  }

  private fun createLayoutCompleter(type: NodeType): IXmlCompletionProvider? {
    return when (type) {
      TAG -> LayoutTagCompletionProvider(this)
      ATTRIBUTE -> LayoutAttrCompletionProvider(this)
      ATTRIBUTE_VALUE -> AttrValueCompletionProvider(this)
      else -> null
    }
  }
}

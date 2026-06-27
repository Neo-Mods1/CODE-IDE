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



package com.neo.ide.lsp.xml.providers.snippet

import com.neo.ide.lsp.snippets.ISnippetScope

val XML_SNIPPET_SCOPES : Array<IXmlSnippetScope> =
  arrayOf(
    DefaultXmlSnippetScope(XmlResourceType.LAYOUT, XmlScope.TAG),
    DefaultXmlSnippetScope(XmlResourceType.LAYOUT, XmlScope.INSIDE),
    DefaultXmlSnippetScope(XmlResourceType.MANIFEST, XmlScope.INSIDE)
  )

abstract class IXmlSnippetScope : ISnippetScope {
  abstract val type: XmlResourceType
  abstract val scope: XmlScope

  override val filename: String
    get() = "${type.name.lowercase()}-${scope.name.lowercase()}"
}

class DefaultXmlSnippetScope(override val type: XmlResourceType, override val scope: XmlScope) :
  IXmlSnippetScope()

enum class XmlResourceType {
  LAYOUT,
  MANIFEST
}

enum class XmlScope {
  TAG,
  INSIDE
}

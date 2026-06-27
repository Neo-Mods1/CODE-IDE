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

import com.neo.ide.lookup.Lookup
import com.neo.ide.lsp.models.CompletionItem.Companion.matchLevel
import com.neo.ide.lsp.models.MatchLevel
import com.neo.ide.lsp.models.MatchLevel.NO_MATCH
import com.neo.ide.xml.res.IResourceTable
import com.neo.ide.xml.resources.ResourceTableRegistry
import org.eclipse.lemminx.dom.DOMNode
import kotlin.math.min

fun match(simpleName: String, qualifiedName: String, prefix: String): MatchLevel {
  val simpleNameMatchLevel = matchLevel(simpleName, prefix)
  val nameMatchLevel = matchLevel(qualifiedName, prefix)
  if (simpleNameMatchLevel == NO_MATCH && nameMatchLevel == NO_MATCH) {
    return NO_MATCH
  }

  return MatchLevel.values()[min(simpleNameMatchLevel.ordinal, nameMatchLevel.ordinal)]
}

fun platformResourceTable(): IResourceTable? {
  return Lookup.getDefault().lookup(ResourceTableRegistry.COMPLETION_FRAMEWORK_RES)
}

fun findAllNamespaces(node: DOMNode): MutableSet<Pair<String, String>> {
  val namespaces = mutableSetOf<Pair<String, String>>()
  var curr: DOMNode? = node

  while (curr != null && !curr.isOwnerDocument) {

    if (curr.attributes == null) {
      curr = curr.parentNode
      continue
    }

    for (i in 0 until curr.attributes.length) {
      val currAttr = curr.getAttributeAtIndex(i)
      if (currAttr.isXmlns) {
        namespaces.add(currAttr.localName to currAttr.value)
      }
    }
    curr = curr.parentNode
  }
  return namespaces
}

/**
 * Transforms entry name to tag name.
 *
 * For example: `AndroidManifestUsesPermission` -> `uses-permission`
 */
fun transformToTagName(entryName: String, prefix: String = ""): String {
  val name = StringBuilder()
  var index = prefix.length
  while (index < entryName.length) {
    var c = entryName[index]
    if (c.isUpperCase()) {
      if (index != prefix.length) {
        name.append('-')
      }
      c = c.lowercaseChar()
    }

    name.append(c)
    ++index
  }
  return name.toString()
}

/**
 * Transforms tag name to entry name.
 *
 * For example: `uses-permission` -> `AndroidManifestUsesPermission`
 */
fun transformToEntryName(tagName: String, prefix: String = ""): String {
  if (tagName == "manifest") {
    return MANIFEST_TAG_PREFIX
  }

  val name = StringBuilder(prefix)

  var index = 0
  var capitalize = false
  while (index < tagName.length) {
    var c = tagName[index]
    if (c == '-') {
      capitalize = true
      ++index
      continue
    }
    if (index == 0 || capitalize) {
      c = c.uppercaseChar()
      capitalize = false
    }
    name.append(c)
    ++index
  }

  return name.toString()
}

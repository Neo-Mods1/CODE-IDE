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



package com.neo.ide.lsp.snippets

import com.google.gson.JsonParseException
import com.google.gson.stream.JsonReader
import com.neo.ide.app.BaseApplication
import com.neo.ide.tasks.executeAsyncProvideError
import com.neo.ide.utils.VMUtils
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

/**
 * Parser for parsing snippets from assets.
 *
 * @author Akash Yadav
 */
object SnippetParser {

  private val log = LoggerFactory.getLogger(SnippetParser::class.java)

  fun <S : ISnippetScope> parse(
    lang: String,
    scopes: Array<S>,
    snippetFactory: (String, String, List<String>) -> ISnippet = { prefix, desc, body ->
      DefaultSnippet(prefix, desc, body.toTypedArray())
    }
  ): Map<S, List<ISnippet>> {

    // not supported for tests as assets cannot be accessed
    if (VMUtils.isJvm()) {
      return emptyMap()
    }

    return ConcurrentHashMap<S, List<ISnippet>>().apply {
      for (scope in scopes) {
        this[scope] =
          mutableListOf<ISnippet>().apply {
            readSnippets(lang, scope.filename, snippetFactory, this)
          }
      }
    }
  }

  private fun readSnippets(
    lang: String,
    type: String,
    snippetFactory: (String, String, List<String>) -> ISnippet,
    snippets: MutableList<ISnippet>
  ) {
    executeAsyncProvideError({
      val content =
        try {
          BaseApplication.getBaseInstance()
            .assets
            .open(assetsPath(lang, type))
            .reader()
        } catch (e: IOException) {
          // snippet file probably does not exist
          return@executeAsyncProvideError
        }

      JsonReader(content).use {
        it.beginObject()
        while (it.hasNext()) {
          val prefix = it.nextName()
          readSnippet(prefix, it, snippetFactory, snippets)
        }
        it.endObject()
      }
    }) { result, err ->
      if (result == null || err != null) {
        log.error("Failed to load '{}' snippets", type, err)
      }
    }
  }

  fun assetsPath(lang: String, type: String) =
    "data/editor/${lang}/snippets.${type}.json"

  private fun readSnippet(
    prefix: String,
    reader: JsonReader,
    snippetFactory: (String, String, List<String>) -> ISnippet,
    snippets: MutableList<ISnippet>
  ) {
    reader.beginObject()
    var desc: String? = null
    val body = mutableListOf<String>()
    while (reader.hasNext()) {
      val n = reader.nextName()
      if (n != "desc" && n != "body") {
        throw JsonParseException("'desc' or 'body' was expected, but found '${n}'")
      }

      if (n == "desc") {
        desc = reader.nextString()
        continue
      }

      if (n == "body") {
        reader.beginArray()
        while (reader.hasNext()) {
          body.add(reader.nextString())
        }
        reader.endArray()
      }
    }

    checkNotNull(desc) { "DefaultSnippet description not defined for '${prefix}'" }
    check(body.isNotEmpty()) { "DefaultSnippet body not defined for '${prefix}'" }

    snippets.add(snippetFactory(prefix, desc, body))

    reader.endObject()
  }
}

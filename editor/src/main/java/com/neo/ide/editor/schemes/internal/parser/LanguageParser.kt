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



package com.neo.ide.editor.schemes.internal.parser

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken.BEGIN_OBJECT
import com.google.gson.stream.JsonToken.STRING
import com.neo.ide.editor.schemes.IDEColorScheme
import com.neo.ide.editor.schemes.LanguageScheme
import com.neo.ide.editor.schemes.StyleDef

/**
 * Parses language definitions in JSON color scheme files.
 *
 * @author Akash Yadav
 */
class LanguageParser(private var reader: JsonReader) {

  fun parseLang(scheme: IDEColorScheme): LanguageScheme {
    return scheme.run {
      doParseLang()
    }
  }

  private fun IDEColorScheme.doParseLang(): LanguageScheme {
    reader.beginObject()
    val lang = LanguageScheme()
    while (reader.hasNext()) {
      var name = reader.nextName()
      when (name) {
        "types" -> parseLangTypes(lang)
        "local.scopes" -> parseLangLocalScopes(lang)
        "local.scopes.members" -> parseLangLocalsMembersScopes(lang)
        "local.definitions" -> parseLocalLangDefs(lang)
        "local.definitions.values" -> parseLocalLangDefVals(lang)
        "local.references" -> parseLocalLangRefs(lang)
        "styles" -> {
          reader.beginObject()
          while (reader.hasNext()) {
            name = reader.nextName()
            if (reader.peek() == BEGIN_OBJECT) {
              lang.styles[name] = parseStyleDef(reader)
            } else if (reader.peek() == STRING) {
              val color = parseColorValue(reader.nextString())
              lang.styles[name] = StyleDef(fg = color)
            } else throw ParseException("A style definition must an object or a string value")
          }
          reader.endObject()
        }
        else -> throw ParseException("Unexpected key '$name' in language object")
      }
    }
    reader.endObject()

    if (lang.files.isEmpty()) {
      throw ParseException("A language must specify the file types")
    }

    return lang
  }

  private fun parseLocalLangRefs(lang: LanguageScheme) {
    addArrStrings(lang.localRefs)
  }

  private fun parseLocalLangDefVals(lang: LanguageScheme) {
    addArrStrings(lang.localDefVals)
  }

  private fun parseLocalLangDefs(lang: LanguageScheme) {
    addArrStrings(lang.localDefs)
  }

  private fun parseLangLocalScopes(lang: LanguageScheme) {
    addArrStrings(lang.localScopes)
  }
  
  private fun parseLangLocalsMembersScopes(lang: LanguageScheme) {
    addArrStrings(lang.localMembersScopes)
  }

  private fun parseLangTypes(lang: LanguageScheme) {
    addArrStrings(lang.files)
  }

  private fun addArrStrings(collection: MutableCollection<String>) {
    reader.beginArray()
    while (reader.hasNext()) {
      collection.add(reader.nextString())
    }
    reader.endArray()
  }
}

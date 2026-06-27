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



package com.neo.ide.editor.language.incremental

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.TokenSource

/**
 * Token used for incremental lexing tasks. Allows us to store other information to tokens.
 *
 * @author Akash Yadav
 */
class IncrementalToken(val token: Token) : Token {

  @JvmField var type = token.type
  @JvmField var startIndex = token.startIndex
  @JvmField var incomplete = false

  override fun getText(): String = token.text
  override fun getType() = type
  override fun getLine() = token.line
  override fun getCharPositionInLine() = token.charPositionInLine
  override fun getChannel() = token.channel
  override fun getTokenIndex() = token.tokenIndex
  override fun getStartIndex() = startIndex
  override fun getStopIndex() = token.stopIndex
  override fun getTokenSource(): TokenSource = token.tokenSource
  override fun getInputStream(): CharStream = token.inputStream
  override fun equals(other: Any?) = token == other
  override fun hashCode() = token.hashCode()
  override fun toString() = token.toString()
}

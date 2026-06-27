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



package com.neo.ide.lsp.java.models

import com.neo.ide.lsp.edits.IEditHandler
import com.neo.ide.lsp.java.edits.BaseJavaEditHandler
import com.neo.ide.lsp.models.Command
import com.neo.ide.lsp.models.CompletionItem
import com.neo.ide.lsp.models.CompletionItemKind
import com.neo.ide.lsp.models.ICompletionData
import com.neo.ide.lsp.models.InsertTextFormat
import com.neo.ide.lsp.models.MatchLevel
import com.neo.ide.lsp.models.TextEdit

/**
 * Completion item model for java completion items.
 *
 * @author Akash Yadav
 */
class JavaCompletionItem(
  label: String,
  detail: String,
  insertText: String?,
  insertTextFormat: InsertTextFormat?,
  sortText: String?,
  command: Command?,
  kind: CompletionItemKind,
  matchLevel: MatchLevel,
  additionalTextEdits: List<TextEdit>?,
  data: ICompletionData?,

  // Override the default edit handler
  editHandler: IEditHandler = BaseJavaEditHandler()
) :
  CompletionItem(
    label,
    detail,
    insertText,
    insertTextFormat,
    sortText,
    command,
    kind,
    matchLevel,
    additionalTextEdits,
    data,
    editHandler
  ) {

  constructor() :
    this(
      "", // label
      "", // detail
      null, // insertText
      null, // insertTextFormat
      null, // sortText
      null, // command
      CompletionItemKind.NONE, // kind
      MatchLevel.NO_MATCH, // match level
      ArrayList(), // additionalEdits
      null // data
    )
}

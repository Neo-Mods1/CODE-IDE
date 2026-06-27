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



package com.neo.ide.lsp.models

import com.neo.ide.lsp.models.CodeActionKind.None
import java.nio.file.Path

/**
 * Parameter provided to the language client in order to perform a code action.
 *
 * @property async Whether the client should peform the code action asynchronously. If this is true,
 *   the text edits will be done on a background thread and a progress sheet will be shown
 *   throughout the process.
 * @property action The code action to perform.
 */
data class PerformCodeActionParams
@JvmOverloads
constructor(val async: Boolean = true, val action: CodeActionItem)

/**
 * @property command The command to execute after the action is performed. This action is always
 *   performed in the currently opened editor, irrespective of the changes specified in [changes].
 */
data class CodeActionItem(
  var title: String,
  var changes: List<DocumentChange>,
  var kind: CodeActionKind,
  var command: Command
) {
  constructor() : this("", ArrayList(), None, Command("", ""))
}

enum class CodeActionKind {
  QuickFix,
  None
}

data class DocumentChange(var file: Path?, var edits: List<TextEdit>) {
  constructor() : this(null, ArrayList())
}

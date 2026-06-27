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



package com.neo.ide.editor.api

import com.neo.ide.lsp.api.ILanguageClient
import com.neo.ide.lsp.api.ILanguageServer
import com.neo.ide.lsp.models.Command
import com.neo.ide.lsp.models.SignatureHelp

/**
 * LSP functions for the editor.
 *
 * @author Akash Yadav
 */
interface ILspEditor {
  /**
   * Set the language server that this editor will connect with. If the language client is not set,
   * it'll be set to [ILanguageClient] from the language server.
   *
   * @param server The server to set. Provide `null` to disable all the language server features.
   */
  fun setLanguageServer(server: ILanguageServer?)

  /**
   * Set the language client to this editor.
   *
   * @param client The client to set.
   */
  fun setLanguageClient(client: ILanguageClient?)

  /**
   * Execute the given LSP command in the editor.
   *
   * @param command The command to execute.
   */
  fun executeCommand(command: Command?)

  /**
   * If any language server is set, requests signature help at the cursor's position. On a valid
   * response, shows the signature help in a popup window.
   */
  fun signatureHelp()

  /**
   * Shows the given signature help in the editor.
   *
   * @param help The signature help data to show.
   */
  fun showSignatureHelp(help: SignatureHelp?)

  /**
   * If any language server is set, asks the language server to find the definition of token at the
   * cursor position.
   *
   * If the server returns a valid response, and the file specified in the response is same the file
   * in this editor, the range specified in the response will be selected.
   */
  fun findDefinition()

  /**
   * If any language server instance is set, finds the references to of the token at the current
   * cursor position.
   *
   * If the server returns a valid response, that response is forwarded to the [ ].
   */
  fun findReferences()

  /**
   * Requests the language server to provided a semantically larger selection than the current
   * selection. If a valid response is received, that range will be selected.
   */
  fun expandSelection()

  /** Ensures that all the windows are dismissed. */
  fun ensureWindowsDismissed()
}
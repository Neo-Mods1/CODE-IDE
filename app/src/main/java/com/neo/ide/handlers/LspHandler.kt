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



package com.neo.ide.handlers

import com.neo.ide.lsp.api.ILanguageClient
import com.neo.ide.lsp.api.ILanguageServerRegistry
import com.neo.ide.lsp.java.JavaLanguageServer
import com.neo.ide.lsp.xml.XMLLanguageServer

/**
 *
 * @author Akash Yadav
 */
object LspHandler {

  fun registerLanguageServers() {
    ILanguageServerRegistry.getDefault().apply {
      getServer(JavaLanguageServer.SERVER_ID) ?: register(JavaLanguageServer())
      getServer(XMLLanguageServer.SERVER_ID) ?: register(XMLLanguageServer())
    }
  }
  
  fun connectClient(client: ILanguageClient) {
    ILanguageServerRegistry.getDefault().connectClient(client)
  }

  fun destroyLanguageServers(isConfigurationChange: Boolean) {
    if (isConfigurationChange) {
      return
    }
    ILanguageServerRegistry.getDefault().destroy()
  }
}

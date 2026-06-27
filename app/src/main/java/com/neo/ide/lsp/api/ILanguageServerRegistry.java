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



package com.neo.ide.lsp.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A language server registry which keeps track of registered language servers.
 *
 * @author Akash Yadav
 */
public abstract class ILanguageServerRegistry {

  private static ILanguageServerRegistry sRegistry = null;

  public static ILanguageServerRegistry getDefault() {
    if (sRegistry == null) {
      sRegistry = new DefaultLanguageServerRegistry();
    }

    return sRegistry;
  }

  /**
   * Register the language server.
   *
   * @param server The server to register.
   */
  public abstract void register(@NonNull ILanguageServer server);

  /** Connects client to all the registered {@link ILanguageServer}s. */
  public abstract void connectClient(@NonNull ILanguageClient client);

  /**
   * Unregister the given server. If any server is registered with the given server ID, a shutdown
   * request will be sent to that server.
   *
   * @param serverId The ID of the server to unregister.
   */
  public abstract void unregister(@NonNull String serverId);

  /** Calls {@link #unregister(String)} for all the registered language servers. */
  public abstract void destroy();

  /**
   * Get the {@link ILanguageServer} registered with the given server ID.
   *
   * @param serverId The ID of the language server.
   * @return The {@link ILanguageServer} instance. Or <code>null</code> if no server is registered
   *     with the provided ID.
   */
  @Nullable
  public abstract ILanguageServer getServer(@NonNull String serverId);
}

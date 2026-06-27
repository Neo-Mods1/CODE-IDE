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

import com.neo.ide.lsp.models.CompletionsKt;
import com.neo.ide.preferences.internal.EditorPreferences;

/**
 * Settings contain preferences for the language server. Clients can use settings to enable/disable
 * specific features of a server.
 *
 * @author Akash Yadav
 */
public interface IServerSettings {

  String KEY_COMPLETIONS_MATCH_LOWER = EditorPreferences.COMPLETIONS_MATCH_LOWER;

  /**
   * Called by language server to check if the completions are enabled. If not enabled, the server
   * should not was time and memory computing completions.
   *
   * @return {@code true} if enabled, {@code false} otherwise.
   */
  boolean completionsEnabled();

  /**
   * Called by the language server to check if the source code analysis is enabled or not.
   *
   * @return {@code true} if enabled, {@code false} otherwise.
   */
  default boolean diagnosticsEnabled() {
    return true;
  }

  /**
   * Called by the language server to check if the code actions are enabled.
   *
   * @return {@code true} if enabled, {@code false} otherwise.
   */
  boolean codeActionsEnabled();

  /**
   * Called by language server to check if smart selections are enabled or not.
   *
   * @return {@code true} if enabled, {@code false} otherwise.
   */
  boolean smartSelectionsEnabled();

  /**
   * Called by the language server to check if the signature help is enabled.
   *
   * @return {@code true} if enabled, {@code false} otherwise.
   */
  boolean signatureHelpEnabled();

  /**
   * Called by the language server to check if finding references is enabled.
   *
   * @return {@code true} if enabled, {@code false} otherwise.
   */
  boolean referencesEnabled();

  /**
   * Called by the language server to check if finding definitions is enabled.
   *
   * @return {@code true} if enabled, {@code false} otherwise.
   */
  boolean definitionsEnabled();

  /**
   * Called by the language server to check if code analysis is enabled.
   *
   * @return {@code true} if enabled, {@code false} otherwise.
   */
  boolean codeAnalysisEnabled();

  /**
   * Called by the completions provider to check if it should match partial names in all lowercase.
   *
   * @return {@code true} if enabled, {@code false} otherwise.
   */
  boolean shouldMatchAllLowerCase();

  /**
   * The minimum match ratio that is needed for the completions to be considered as 'matched'.
   *
   * @return The match ratio. Betweeen 0 and 100.
   */
  default int completionFuzzyMatchMinRatio() {
    return CompletionsKt.DEFAULT_MIN_MATCH_RATIO;
  }
}

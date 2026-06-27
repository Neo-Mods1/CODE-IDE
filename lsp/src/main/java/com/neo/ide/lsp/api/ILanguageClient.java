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

import androidx.annotation.Nullable;
import com.neo.ide.lsp.models.CodeActionItem;
import com.neo.ide.lsp.models.DiagnosticItem;
import com.neo.ide.lsp.models.DiagnosticResult;
import com.neo.ide.lsp.models.PerformCodeActionParams;
import com.neo.ide.lsp.models.ShowDocumentParams;
import com.neo.ide.lsp.models.ShowDocumentResult;
import com.neo.ide.models.Location;
import java.io.File;
import java.util.List;

/**
 * A language client handles notifications and events from a {@link ILanguageServer}.
 *
 * @author Akash Yadav
 */
public interface ILanguageClient {

  /**
   * Publish the diagnostics result (allow the user to see them).
   *
   * @param result The diagnostic result.
   */
  void publishDiagnostics(DiagnosticResult result);

  /**
   * Get the diagnostic item in the given file at the given character position.
   *
   * @param file   The file to search diagnostics in.
   * @param line   The line.
   * @param column The column.
   * @return The diagnostic item or <code>null</code> if none was found.
   */
  @Nullable
  DiagnosticItem getDiagnosticAt(File file, int line, int column);

  /**
   * Perform the given code action.
   *
   * @param params The parameters describing the actions to perform.
   */
  void performCodeAction(PerformCodeActionParams params);

  default void performCodeAction(CodeActionItem actionItem) {
    if (actionItem == null) {
      return;
    }
    performCodeAction(new PerformCodeActionParams(actionItem));
  }

  /**
   * Perform the given code action.
   *
   * @param file       The file in which the given action must be performed.
   * @param actionItem The action item describing the action.
   */
  @Deprecated
  default void performCodeAction(File file, CodeActionItem actionItem) {
    performCodeAction(actionItem);
  }

  /**
   * Notification sent by the language server to tell the client that it should open the given file
   * and select the range from the params.
   *
   * @param params The params for showing the document.
   * @return The result of the show document request. Servers can use this result to perform further
   * action.
   */
  ShowDocumentResult showDocument(ShowDocumentParams params);

  /**
   * Notification sent by the language server to tell teh client client to show the given locations
   * to the user.
   *
   * @param locations The location to show.
   */
  void showLocations(List<Location> locations);
}

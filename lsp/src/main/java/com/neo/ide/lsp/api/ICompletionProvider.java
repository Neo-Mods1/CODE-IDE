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
import com.neo.ide.lookup.Lookup;
import com.neo.ide.lsp.models.CompletionItem;
import com.neo.ide.lsp.models.CompletionParams;
import com.neo.ide.lsp.models.CompletionResult;
import com.neo.ide.lsp.models.CompletionsKt;
import com.neo.ide.lsp.models.MatchLevel;
import com.neo.ide.progress.ICancelChecker;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A completion provider provides completions at the given line and column.
 *
 * @author Akash Yadav
 */
public interface ICompletionProvider {
  
  default boolean canComplete(Path file) {
    return file != null && Files.exists(file) && !Files.isDirectory(file);
  }

  /** Abort the completion process if cancelled. */
  default void abortCompletionIfCancelled() {
    final var checker = Lookup.getDefault().lookup(ICancelChecker.class);
    if (checker != null) {
      checker.abortIfCancelled();
    }
  }

  default MatchLevel matchLevel(CharSequence candidate, CharSequence partial) {
    var matchRatio = CompletionsKt.DEFAULT_MIN_MATCH_RATIO;
    if (this instanceof AbstractServiceProvider) {
      matchRatio = ((AbstractServiceProvider) this).getSettings().completionFuzzyMatchMinRatio();
    }

    if (matchRatio < 0 || matchRatio > 100) matchRatio = CompletionsKt.DEFAULT_MIN_MATCH_RATIO;

    return CompletionItem.matchLevel(candidate.toString(), partial.toString(), matchRatio);
  }

  /**
   * Compute completions using the given params and return the given completion result.
   *
   * @param params The params that can be used to compute completion items.
   * @return The completion result. Must not be null.
   */
  @NonNull
  CompletionResult complete(CompletionParams params);
}

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



package com.neo.ide.lsp.util;

import androidx.annotation.Nullable;
import com.neo.ide.app.BaseApplication;
import com.neo.ide.lsp.api.IServerSettings;
import com.neo.ide.managers.PreferenceManager;

/**
 * {@link IServerSettings} implementation which uses {@link
 * com.neo.ide.managers.PreferenceManager PreferencesManager} to read common settings.
 *
 * @author Akash Yadav
 */
public abstract class PrefBasedServerSettings extends DefaultServerSettings {

  private PreferenceManager prefs;

  @Override
  public boolean shouldMatchAllLowerCase() {
    final var prefs = getPrefs();
    if (prefs != null) {
      return prefs.getBoolean(KEY_COMPLETIONS_MATCH_LOWER, true);
    }

    return false;
  }

  @Nullable
  public PreferenceManager getPrefs() {
    if (prefs == null) {
      final var app = BaseApplication.getBaseInstance();
      if (app != null) {
        prefs = app.getPrefManager();
      }
    }
    return prefs;
  }
}

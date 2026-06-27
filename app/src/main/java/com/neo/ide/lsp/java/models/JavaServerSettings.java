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


package com.neo.ide.lsp.java.models;

import androidx.annotation.NonNull;
import com.google.googlejavaformat.java.JavaFormatterOptions;
import com.neo.ide.lsp.util.PrefBasedServerSettings;
import com.neo.ide.managers.PreferenceManager;
import com.neo.ide.preferences.internal.JavaPreferences;
import com.neo.ide.utils.VMUtils;

/**
 * Server settings for the java language server.
 *
 * @author Akash Yadav
 */
public class JavaServerSettings extends PrefBasedServerSettings {

  public static final String KEY_JAVA_PREF_GOOGLE_CODE_STYLE = JavaPreferences.GOOGLE_CODE_STYLE;
  public static final int CODE_STYLE_AOSP = 0;
  public static final int CODE_STYLE_GOOGLE = 1;
  private static JavaServerSettings instance;

  @NonNull
  public static JavaServerSettings getInstance() {
    if (instance == null) {
      instance = new JavaServerSettings();
    }

    return instance;
  }

  @Override
  public boolean diagnosticsEnabled() {
    return VMUtils.isJvm() || JavaPreferences.INSTANCE.isJavaDiagnosticsEnabled();
  }

  public JavaFormatterOptions getFormatterOptions() {
    return JavaFormatterOptions.builder().formatJavadoc(true).style(getStyle()).build();
  }

  public JavaFormatterOptions.Style getStyle() {
    if (getCodeStyle() == JavaServerSettings.CODE_STYLE_AOSP) {

      return JavaFormatterOptions.Style.AOSP;
    }

    return JavaFormatterOptions.Style.GOOGLE;
  }

  private int getCodeStyle() {
    final PreferenceManager prefs = getPrefs();
    if (prefs != null) {
      if (prefs.getBoolean(KEY_JAVA_PREF_GOOGLE_CODE_STYLE, false)) {
        return CODE_STYLE_GOOGLE;
      }
    }

    return CODE_STYLE_AOSP;
  }
}

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



package com.neo.ide.lsp.java.utils;

import androidx.annotation.NonNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Extractors {

  private static final Pattern PACKAGE_EXTRACTOR =
      Pattern.compile("^([a-z][_a-zA-Z0-9]*\\.)*[a-z][_a-zA-Z0-9]*");
  private static final Pattern SIMPLE_EXTRACTOR = Pattern.compile("[A-Z][_a-zA-Z0-9]*$");

  @NonNull
  public static String packageName(String className) {
    Matcher matcher = PACKAGE_EXTRACTOR.matcher(className);
    if (matcher.find()) {
      return matcher.group();
    }
    return "";
  }

  @NonNull
  public static String simpleName(String className) {
    Matcher matcher = SIMPLE_EXTRACTOR.matcher(className);
    if (matcher.find()) {
      return matcher.group();
    }
    return "";
  }
}

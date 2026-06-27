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



package com.neo.ide.lsp.xml.providers.format;

import com.neo.ide.lsp.models.TextEdit;
import com.neo.ide.models.Range;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * XML formatter support.
 */
public class XMLFormatter {

  private static final Logger LOG = LoggerFactory.getLogger(XMLFormatter.class);

  /**
   * Returns a List containing a single TextEdit, containing the newly formatted changes of the
   * document.
   *
   * @param range specified range in which formatting will be done
   * @return List containing a TextEdit with formatting changes
   */
  public List<? extends TextEdit> format(DOMDocument xmlDocument, Range range) {
    try {
      XMLFormatterDocument formatterDocument =
          new XMLFormatterDocument(xmlDocument.getTextDocument(), range);
      return formatterDocument.format();
    } catch (BadLocationException e) {
      LOG.error("Formatting failed due to BadLocation", e);
    }
    return null;
  }
}

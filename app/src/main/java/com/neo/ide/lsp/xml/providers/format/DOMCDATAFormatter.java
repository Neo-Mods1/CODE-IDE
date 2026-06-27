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



/*******************************************************************************
 * Copyright (c) 2022 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package com.neo.ide.lsp.xml.providers.format;

import com.neo.ide.lsp.models.TextEdit;
import com.neo.ide.preferences.internal.EditorPreferences;

import org.eclipse.lemminx.dom.DOMCDATASection;

import java.util.List;

/** DOM CDATA section formatter. */
public class DOMCDATAFormatter {
  private final XMLFormatterDocumentNew formatterDocument;

  public DOMCDATAFormatter(XMLFormatterDocumentNew formatterDocument) {
    this.formatterDocument = formatterDocument;
  }

  public void formatCDATASection(
      DOMCDATASection cDATANode, XMLFormattingConstraints parentConstraints, List<TextEdit> edits) {

    String text = formatterDocument.getText();
    int availableLineWidth = parentConstraints.getAvailableLineWidth();
    int start = cDATANode.getStart();
    int leftWhitespaceOffset = start > 0 ? start - 1 : 0;
    int spaceStart = -1;
    int spaceEnd = -1;

    while (leftWhitespaceOffset > 0 && Character.isWhitespace(text.charAt(leftWhitespaceOffset))) {
      leftWhitespaceOffset--;
    }

    if (isJoinCDATALines()) {
      int contentEnd = -1;
      int cDATAStartContent = cDATANode.getStartContent();
      int cDATAEndContent = cDATANode.getEndContent();
      for (int i = cDATAStartContent; i <= cDATAEndContent; i++) {
        char c = text.charAt(i);
        if (Character.isWhitespace(c)) {
          // Whitespaces
          if (spaceStart == -1) {
            spaceStart = i;
          } else {
            spaceEnd = i;
          }
        } else {
          int contentStart = i;
          while (i < cDATAEndContent && !Character.isWhitespace(text.charAt(i + 1))) {
            i++;
          }
          contentEnd = i;
          availableLineWidth -= (contentEnd + 1 - contentStart);
          if (availableLineWidth <= 0) {
            if (spaceStart != -1) {
              // Add new line when the comment extends over the maximum line width
              replaceLeftSpacesWithIndentation(
                  parentConstraints.getIndentLevel(), spaceStart, contentStart, true, edits);
              int indentSpaces = (getTabSize() * parentConstraints.getIndentLevel());
              availableLineWidth =
                  getMaxLineWidth() - indentSpaces - (contentEnd + 1 - contentStart);
            }
          } else if (spaceStart == cDATAStartContent) {
            // Remove spaces before the start bracket of content
            removeLeftSpaces(spaceStart, contentStart, edits);
            spaceStart = -1;
            spaceEnd = -1;
          } else if (contentEnd == cDATAEndContent) {
            // Remove spaces after the ending bracket of content
            removeLeftSpaces(spaceStart, contentEnd, edits);
            spaceStart = -1;
            spaceEnd = -1;
          } else {
            // Normalize space between content
            replaceSpacesWithOneSpace(spaceStart, spaceEnd, edits);
            availableLineWidth--;
            spaceStart = -1;
            spaceEnd = -1;
          }
        }
      }
    }
  }

  private void removeLeftSpaces(int from, int to, List<TextEdit> edits) {
    formatterDocument.removeLeftSpaces(from, to, edits);
  }

  private boolean isJoinCDATALines() {
    return formatterDocument.getSharedSettings().getFormattingOptions().isJoinCDataLines();
  }

  private int getTabSize() {
    return EditorPreferences.INSTANCE.getTabSize();
  }

  private int getMaxLineWidth() {
    return formatterDocument.getMaxLineWidth();
  }

  private void replaceSpacesWithOneSpace(int spaceStart, int spaceEnd, List<TextEdit> edits) {
    formatterDocument.replaceSpacesWithOneSpace(spaceStart, spaceEnd, edits);
  }

  private int replaceLeftSpacesWithIndentation(
      int indentLevel, int from, int to, boolean addLineSeparator, List<TextEdit> edits) {
    return formatterDocument.replaceLeftSpacesWithIndentation(
        indentLevel, from, to, addLineSeparator, edits);
  }
}

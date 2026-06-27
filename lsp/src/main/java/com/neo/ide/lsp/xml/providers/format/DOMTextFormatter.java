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

import org.eclipse.lemminx.dom.DOMText;

import java.util.List;

/**
 * DOM text formatter.
 *
 * @author Angelo ZERR
 */
public class DOMTextFormatter {

  private final XMLFormatterDocumentNew formatterDocument;

  public DOMTextFormatter(XMLFormatterDocumentNew formatterDocument) {
    this.formatterDocument = formatterDocument;
  }

  public void formatText(
      DOMText textNode, XMLFormattingConstraints parentConstraints, List<TextEdit> edits) {
    // Don't format the spacing in text for case of preserve empty content setting
    if (isPreserveEmptyContent()) {
      return;
    }
    FormatElementCategory formatElementCategory = parentConstraints.getFormatElementCategory();
    String text = formatterDocument.getText();
    int availableLineWidth = parentConstraints.getAvailableLineWidth();

    int spaceStart = -1;
    int spaceEnd = -1;
    boolean containsNewLine = false;

    for (int i = textNode.getStart(); i < textNode.getEnd(); i++) {
      char c = text.charAt(i);
      if (Character.isWhitespace(c)) {
        // Whitespaces...
        if (isLineSeparator(c)) {
          containsNewLine = true;
        }
        if (spaceStart == -1) {
          spaceStart = i;
        } else {
          spaceEnd = i;
        }
      } else {
        // Text content...
        int contentStart = i;
        while (i < textNode.getEnd() + 1 && !Character.isWhitespace(text.charAt(i + 1))) {
          i++;
        }

        int contentEnd = i;
        availableLineWidth -= (contentEnd - contentStart);

        if (formatElementCategory != FormatElementCategory.PreserveSpace
            && formatElementCategory != FormatElementCategory.IgnoreSpace) {
          if (availableLineWidth <= 0) {
            if (spaceStart != -1) {
              insertLineBreak(spaceStart, contentStart, edits);
              availableLineWidth = getMaxLineWidth();
            }
          } else if (isJoinContentLines()
              || (spaceStart == textNode.getStart() || !containsNewLine)) {
            // Case of isJoinContent == true: join all text content with single space
            // Case of isJoinContent == false: normalize space only between element start
            // tag and start of text content or doesn't contain a new line
            replaceSpacesWithOneSpace(spaceStart, spaceEnd, edits);
            containsNewLine = false;
          }
        }

        spaceStart = -1;
        spaceEnd = -1;
      }
    }
    if (formatElementCategory != FormatElementCategory.PreserveSpace
        && formatElementCategory != FormatElementCategory.IgnoreSpace) {
      replaceSpacesWithOneSpace(spaceStart, spaceEnd, edits);
    }
  }

  private static boolean isLineSeparator(char c) {
    return c == '\r' || c == '\n';
  }

  private int getMaxLineWidth() {
    return formatterDocument.getMaxLineWidth();
  }

  private void insertLineBreak(int start, int end, List<TextEdit> edits) {
    formatterDocument.insertLineBreak(start, end, edits);
  }

  private boolean isPreserveEmptyContent() {
    return formatterDocument.getSharedSettings().getFormattingOptions().isPreserveEmptyContent();
  }

  private void replaceSpacesWithOneSpace(int spaceStart, int spaceEnd, List<TextEdit> edits) {
    formatterDocument.replaceSpacesWithOneSpace(spaceStart, spaceEnd, edits);
  }

  private boolean isJoinContentLines() {
    return formatterDocument.getSharedSettings().getFormattingOptions().isJoinContentLines();
  }
}

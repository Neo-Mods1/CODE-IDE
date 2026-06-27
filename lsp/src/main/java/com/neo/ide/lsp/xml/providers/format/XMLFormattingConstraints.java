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

/**
 * XML formatting constraints.
 *
 * @author Angelo ZERR
 */
public class XMLFormattingConstraints {

  private FormatElementCategory formatElementCategory;

  private int availableLineWidth = 0;
  private int indentLevel = 0;

  /**
   * Initializes the values in this formatting constraint with values from constraints
   *
   * @param constraints cannot be null
   */
  public void copyConstraints(XMLFormattingConstraints constraints) {
    setFormatElementCategory(constraints.getFormatElementCategory());
    setAvailableLineWidth(constraints.getAvailableLineWidth());
    setIndentLevel(constraints.getIndentLevel());
  }

  public FormatElementCategory getFormatElementCategory() {
    return formatElementCategory;
  }

  public void setFormatElementCategory(FormatElementCategory formatElementCategory) {
    this.formatElementCategory = formatElementCategory;
  }

  public int getAvailableLineWidth() {
    return availableLineWidth;
  }

  public void setAvailableLineWidth(int availableLineWidth) {
    this.availableLineWidth = availableLineWidth;
  }

  public int getIndentLevel() {
    return indentLevel;
  }

  public void setIndentLevel(int indentLevel) {
    this.indentLevel = indentLevel;
  }
}

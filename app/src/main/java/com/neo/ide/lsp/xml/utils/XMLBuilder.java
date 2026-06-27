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



/*
 * Copyright (c) 2018 Angelo ZERR All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */

package com.neo.ide.lsp.xml.utils;

import com.neo.ide.lsp.xml.models.XMLServerSettings;

import org.eclipse.lemminx.dom.builder.BaseXmlFormattingOptions;
import org.eclipse.lemminx.dom.builder.XmlBuilder;

/**
 * XML content builder utilities.
 */
public class XMLBuilder extends XmlBuilder {

  public XMLBuilder(String whitespacesIndent, String lineDelimiter) {
    this(whitespacesIndent, lineDelimiter, XMLServerSettings.INSTANCE.getFormattingOptions());
  }

  public XMLBuilder(String whitespacesIndent, String lineDelimiter, BaseXmlFormattingOptions settings
  ) {
    super(whitespacesIndent, lineDelimiter, settings);
  }
}

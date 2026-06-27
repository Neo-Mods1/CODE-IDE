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
 * Format element catagory.
 * 
 * @author Angelo ZERR
 * 
 * @see https://www.oxygenxml.com/doc/versions/24.0/ug-editorEclipse/topics/format-and-indent-xml.html
 */
public enum FormatElementCategory {

	/**
	 * In the Ignore space category, all whitespace is considered insignificant.
	 * This generally applies to content that consists only of elements nested
	 * inside other elements, with no text content.
	 */
	IgnoreSpace,

	/**
	 * In the normalize space category, a single whitespace character between
	 * character strings is considered significant and all other spaces are
	 * considered insignificant. Therefore, all consecutive whitespaces will be
	 * replaced with a single space. This generally applies to elements that contain
	 * text content only.
	 */
	NormalizeSpace,

	/**
	 * In the mixed content category, a single whitespace between text characters is
	 * considered significant and all other spaces are considered insignificant.
	 */
	MixedContent,

	/**
	 * In the preserve space category, all whitespace in the element is regarded as
	 * significant. No changes are made to the spaces in elements in this category.
	 * However, child elements may be in another category, and may be treated
	 * differently.
	 * 
	 * Attribute values are always in the preserve space category. The spaces
	 * between attributes in an element tag are always in the default space
	 * category.
	 * 
	 */
	PreserveSpace
}

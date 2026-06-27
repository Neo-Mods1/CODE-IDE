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



package com.neo.ide.editor.language.treesitter.internal

import com.neo.ide.editor.language.treesitter.TSLanguageRegistry
import com.neo.ide.editor.language.treesitter.TreeSitterLanguage
import java.util.concurrent.ConcurrentHashMap

/**
 * Default implementation of [TSLanguageRegistry].
 *
 * @author Akash Yadav
 */
class TSLanguageRegistryImpl : TSLanguageRegistry {

  private val registry =
    ConcurrentHashMap<String, TreeSitterLanguage.Factory<out TreeSitterLanguage>>()

  override fun <T : TreeSitterLanguage> register(
    fileType: String,
    factory: TreeSitterLanguage.Factory<T>
  ) {
    val older = registry.put(fileType, factory)
    if (older != null) {
      registry[fileType] = older
      throw TSLanguageRegistry.AlreadyRegisteredException(fileType)
    }
  }

  override fun hasLanguage(fileType: String): Boolean {
    return registry.containsKey(fileType)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T : TreeSitterLanguage> getFactory(
    fileType: String
  ): TreeSitterLanguage.Factory<T> {
    return (registry[fileType] ?: throw TSLanguageRegistry.NotRegisteredException(fileType))
        as TreeSitterLanguage.Factory<T>
  }

  override fun destroy() {
    registry.clear()
  }
}

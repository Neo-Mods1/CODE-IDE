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



package com.neo.ide.editor.language.treesitter

import com.neo.ide.editor.language.treesitter.internal.TSLanguageRegistryImpl

/**
 * Registry for managing [TreeSitterLanguage factories][TreeSitterLanguage.Factory].
 *
 * @author Akash Yadav
 */
interface TSLanguageRegistry {

  companion object {

    @JvmStatic
    val instance by lazy { TSLanguageRegistryImpl() }
  }

  /**
   * Registers the given [factory] for the given file types.
   *
   * @param fileType The file extension for which the given factory should be used.
   * @param factory The factory which will create the [TreeSitterLanguage] instance.
   * @throws AlreadyRegisteredException If an instance of [TreeSitterLanguage.Factory] is already
   *   registered for the given file type.
   */
  fun <T : TreeSitterLanguage> register(fileType: String, factory: TreeSitterLanguage.Factory<T>)

  /**
   * Checks whether a [TreeSitterLanguage] has been registered for the given [file type][fileType].
   *
   * @return `true` if a [TreeSitterLanguage] has been registered for [fileType], `false` otherwise.
   */
  fun hasLanguage(fileType: String): Boolean

  /**
   * Returns the instance of the [TreeSitterLanguage.Factory] for the given file type.
   *
   * @param fileType The file type (extension) to create the language factory instance for.
   * @return The [TreeSitterLanguage.Factory] implmementation.
   * @throws NotRegisteredException If no [TreeSitterLanguage.Factory] is registered for the given
   *   file type.
   */
  fun <T : TreeSitterLanguage> getFactory(fileType: String): TreeSitterLanguage.Factory<T>

  /**
   * Destroys the language registry, removing all the registered language factory. This must be
   * called only when the application is exiting.
   */
  fun destroy()

  class AlreadyRegisteredException(type: String) :
    IllegalStateException(
      "An instance of TreeSitterLanguage.Factory is already registered for file type '$type'"
    )

  class NotRegisteredException(type: String) :
    RuntimeException("No TreeSitterLanguage.Factory registered for file type '$type'")
}

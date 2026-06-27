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

package com.neo.ide.utils

import java.util.ServiceLoader as JavaServiceLoader

object ServiceLoader {
    inline fun <reified T : Any> load(type: Class<T>): ServiceLoaderResult<T> {
        return ServiceLoaderResult(JavaServiceLoader.load(type))
    }
}

class ServiceLoaderResult<T>(private val delegate: JavaServiceLoader<T>) {
    fun findFirstOrThrow(): T {
        return delegate.findFirst().orElseThrow {
            throw NoSuchElementException("No service provider found for ${delegate.javaClass.name}")
        }
    }

    fun findFirst(): T? {
        return delegate.findFirst().orElse(null)
    }

    fun findAll(): List<T> {
        return delegate.toList()
    }
}

package com.neo.ide.xml.versions

import com.neo.ide.lookup.Lookup
import com.neo.ide.utils.ServiceLoader
import java.io.File

interface ApiVersionsRegistry {

  companion object {
    private var sInstance: ApiVersionsRegistry? = null

    @JvmStatic
    fun getInstance(): ApiVersionsRegistry {
      val klass = ApiVersionsRegistry::class.java
      return sInstance ?: ServiceLoader.load(klass, klass.classLoader).findFirstOrThrow()
        .also { sInstance = it }
    }
  }

  fun forPlatformDir(platform: File): ApiVersions?
}

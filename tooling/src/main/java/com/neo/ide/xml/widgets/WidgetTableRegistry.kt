package com.neo.ide.xml.widgets

import com.neo.ide.lookup.Lookup
import com.neo.ide.utils.ServiceLoader
import java.io.File

interface WidgetTableRegistry {

  companion object {
    private var sInstance: WidgetTableRegistry? = null

    @JvmStatic
    fun getInstance(): WidgetTableRegistry {
      val klass = WidgetTableRegistry::class.java
      return sInstance ?: ServiceLoader.load(klass, klass.classLoader).findFirstOrThrow()
        .also { sInstance = it }
    }
  }

  fun forPlatformDir(platform: File): WidgetTable?
}

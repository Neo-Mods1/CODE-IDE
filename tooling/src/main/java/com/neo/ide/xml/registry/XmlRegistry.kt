package com.neo.ide.xml.registry

import java.io.File

interface XmlRegistry<T> {
  fun forPlatformDir(platform: File): T?
}

package com.neo.ide.xml.widgets

import java.io.File

class WidgetTable(
  val platform: File,
  val widgets: Map<String, String> = emptyMap()
) {
  fun getWidget(name: String): String? {
    return widgets[name]
  }
}

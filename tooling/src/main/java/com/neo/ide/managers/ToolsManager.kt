package com.neo.ide.managers

import android.content.Context

object ToolsManager {

  private var context: Context? = null

  @JvmStatic
  fun init(context: Context) {
    this.context = context.applicationContext
  }

  @JvmStatic
  fun getCommonAsset(name: String): String {
    return context?.filesDir?.resolve("common")?.resolve(name)?.absolutePath ?: ""
  }
}

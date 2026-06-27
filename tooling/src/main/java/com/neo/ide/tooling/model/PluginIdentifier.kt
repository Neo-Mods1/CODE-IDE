package com.neo.ide.tooling.model

import java.io.Serializable

data class PluginIdentifier(
  val pluginId: String,
  val version: String? = null
) : Serializable

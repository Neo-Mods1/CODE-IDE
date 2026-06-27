package com.neo.ide.tooling.api.models

import java.io.Serializable

open class Launchable(
  val displayName: String?,
  val isPublic: Boolean = false
) : Serializable

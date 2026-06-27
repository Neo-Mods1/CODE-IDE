package com.neo.ide.tooling.api.models

import java.io.File
import java.io.Serializable

open class JavaSourceDirectory(
  val directory: File,
  val isGenerated: Boolean = false
) : Serializable

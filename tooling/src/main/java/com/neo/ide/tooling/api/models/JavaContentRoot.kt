package com.neo.ide.tooling.api.models

import java.io.File
import java.io.Serializable

open class JavaContentRoot(
  val file: File? = null,
  val isTest: Boolean = false
) : Serializable

package com.neo.ide.tooling.model

import java.io.Serializable

data class ProjectIdentifier(
  val buildPath: String,
  val projectPath: String
) : Serializable {
  companion object {
    @JvmField
    val ROOT = ProjectIdentifier(":", ":")
  }
}

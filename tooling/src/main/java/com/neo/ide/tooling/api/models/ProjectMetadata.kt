package com.neo.ide.tooling.api.models

import java.io.Serializable

open class ProjectMetadata(
  val name: String,
  val projectPath: String,
  val projectDir: java.io.File,
  val buildDir: java.io.File
) : Serializable {
  constructor(base: BasicProjectMetadata) : this(
    base.name ?: "",
    base.projectPath,
    base.projectDir,
    base.buildDir
  )
}

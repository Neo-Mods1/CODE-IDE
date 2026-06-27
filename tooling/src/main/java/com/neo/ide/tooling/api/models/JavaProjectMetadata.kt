package com.neo.ide.tooling.api.models

import java.io.File
import java.io.Serializable

open class JavaProjectMetadata(
  val base: BasicProjectMetadata,
  val compilerSettings: JavaModuleCompilerSettings,
  val classesJar: File?
) : ProjectMetadata(base.name ?: "", base.projectPath, base.projectDir, base.buildDir)

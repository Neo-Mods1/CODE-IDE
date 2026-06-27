package com.neo.ide.tooling.api.models

import com.neo.ide.builder.model.IJavaCompilerSettings
import java.io.Serializable

open class JavaModuleCompilerSettings(
  override val javaSourceVersion: String = "11",
  override val javaBytecodeVersion: String = "11"
) : IJavaCompilerSettings(), Serializable

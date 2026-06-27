package com.neo.ide.tooling.api.models

import java.io.Serializable

sealed class JavaModuleDependency : Serializable

open class JavaModuleExternalDependency(
  val compileName: String? = null,
  val packaging: String? = null,
  val gradleArtifact: GradleArtifact? = null
) : JavaModuleDependency()

open class JavaModuleProjectDependency(
  val projectPath: String
) : JavaModuleDependency()

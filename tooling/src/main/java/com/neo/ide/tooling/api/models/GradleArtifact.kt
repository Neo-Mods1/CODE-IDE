package com.neo.ide.tooling.api.models

import java.io.Serializable

open class GradleArtifact(
  val name: String,
  val path: String,
  val repositoryName: String? = null,
  val version: String? = null
) : Serializable

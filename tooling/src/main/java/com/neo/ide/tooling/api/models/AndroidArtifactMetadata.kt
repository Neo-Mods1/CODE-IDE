package com.neo.ide.tooling.api.models

import java.io.Serializable

open class AndroidArtifactMetadata(
  val name: String,
  val compileTaskName: String = "",
  val assembleTaskName: String = "",
  val resGenTaskName: String = "",
  val sourceGenTaskName: String = "",
  val minSdk: Int = 0,
  val targetSdk: Int = 0,
  val maxSdk: Int = 0
) : Serializable

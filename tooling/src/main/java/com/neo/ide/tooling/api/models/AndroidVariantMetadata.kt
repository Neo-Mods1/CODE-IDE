package com.neo.ide.tooling.api.models

import java.io.Serializable

open class AndroidVariantMetadata(
  val name: String,
  val mainArtifact: AndroidArtifactMetadata,
  val flavorName: String? = null,
  val buildType: String? = null,
  val isDefault: Boolean = false
) : BasicAndroidVariantMetadata(name, mainArtifact)

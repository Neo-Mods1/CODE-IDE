package com.neo.ide.tooling.api.models

import com.neo.ide.builder.model.DefaultSourceSetContainer
import com.neo.ide.builder.model.DefaultViewBindingOptions
import java.io.Serializable

open class AndroidProjectMetadata(
  val base: ProjectMetadata,
  val mainSourceSet: DefaultSourceSetContainer? = null,
  val viewBindingOptions: DefaultViewBindingOptions? = null,
  val androidTestVariant: String? = null,
  val unitTestVariant: String? = null
) : ProjectMetadata(base.name, base.projectPath, base.projectDir, base.buildDir)

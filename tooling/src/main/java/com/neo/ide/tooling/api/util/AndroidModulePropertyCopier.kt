package com.neo.ide.tooling.api.util

import com.android.builder.model.v2.ide.SourceProvider
import com.neo.ide.builder.model.DefaultSyncIssue
import com.neo.ide.builder.model.DefaultViewBindingOptions
import com.neo.ide.builder.model.IDESyncIssue

object AndroidModulePropertyCopier {

  @JvmStatic
  fun copy(syncIssue: IDESyncIssue): DefaultSyncIssue {
    return DefaultSyncIssue(
      data = syncIssue.data,
      message = syncIssue.message,
      multiLineMessage = syncIssue.multiLineMessage,
      severity = syncIssue.severity,
      type = syncIssue.type
    )
  }

  @JvmStatic
  fun copy(viewBindingOptions: Any?): DefaultViewBindingOptions {
    return DefaultViewBindingOptions()
  }
}

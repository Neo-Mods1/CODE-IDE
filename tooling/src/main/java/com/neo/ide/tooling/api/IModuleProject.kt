package com.neo.ide.tooling.api

import org.eclipse.lsp4j.jsonrpc.services.JsonSegment

@JsonSegment("module")
interface IModuleProject : IGradleProject {
  fun getPath(): String
}

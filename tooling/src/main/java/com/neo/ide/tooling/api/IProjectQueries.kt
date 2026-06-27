package com.neo.ide.tooling.api

import com.neo.ide.tooling.api.models.GradleTask
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest
import org.eclipse.lsp4j.jsonrpc.services.JsonSegment
import java.util.concurrent.CompletableFuture

@JsonSegment("project")
interface IProjectQueries {

  @JsonRequest
  fun getSubProjects(): CompletableFuture<List<IProject>>

  @JsonRequest
  fun getTasks(): CompletableFuture<List<GradleTask>>
}

package com.neo.ide.shell

import java.util.concurrent.CompletableFuture

class ProcessBuilderConfig {
  var command: List<String> = emptyList()
  var redirectErrorStream: Boolean = false
  var workingDirectory: String? = null
  var environment: Map<String, String> = emptyMap()
}

fun executeProcessAsync(block: ProcessBuilderConfig.() -> Unit): Process {
  val config = ProcessBuilderConfig().apply(block)
  val pb = ProcessBuilder(config.command)
  config.workingDirectory?.let { pb.directory(java.io.File(it)) }
  pb.environment().putAll(config.environment)
  pb.redirectErrorStream(config.redirectErrorStream)
  return pb.start()
}

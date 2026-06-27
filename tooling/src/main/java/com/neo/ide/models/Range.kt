package com.neo.ide.models

data class Range(
  val startLine: Int = 0,
  val startColumn: Int = 0,
  val endLine: Int = 0,
  val endColumn: Int = 0
) {
  companion object {
    @JvmField
    val NONE = Range()
  }
}

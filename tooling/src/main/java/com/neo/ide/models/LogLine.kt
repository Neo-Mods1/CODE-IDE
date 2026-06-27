package com.neo.ide.models

import com.neo.ide.utils.ILogger

data class LogLine(
  val level: ILogger.Level,
  val tag: String,
  val message: String,
  val timestamp: Long = System.currentTimeMillis()
) {
  companion object {
    @JvmStatic
    fun obtain(level: ILogger.Level, tag: String, message: String): LogLine {
      return LogLine(level, tag, message)
    }
  }
}

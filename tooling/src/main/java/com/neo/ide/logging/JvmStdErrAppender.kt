package com.neo.ide.logging

import ch.qos.logback.core.OutputStreamAppender
import ch.qos.logback.core.encoder.Encoder

open class JvmStdErrAppender : OutputStreamAppender<ch.qos.logback.classic.spi.ILoggingEvent>() {

  init {
    setOutputStream(System.err)
  }
}

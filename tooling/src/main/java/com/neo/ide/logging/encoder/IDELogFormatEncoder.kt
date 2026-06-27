package com.neo.ide.logging.encoder

import ch.qos.logback.classic.pattern.MessageConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.encoder.EncoderBase

class IDELogFormatEncoder : EncoderBase<ILoggingEvent>() {

  override fun encode(event: ILoggingEvent): ByteArray {
    val message = buildString {
      append("[${event.level}] ")
      append("${event.loggerName}: ")
      append(event.formattedMessage)
      append("\n")
    }
    return message.toByteArray()
  }

  override fun start() {
    super.start()
  }
}

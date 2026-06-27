package com.neo.ide.utils

interface ILogger {

  enum class Level(val char: Char) {
    VERBOSE('V'),
    DEBUG('D'),
    INFO('I'),
    WARN('W'),
    ERROR('E');

    companion object {
      @JvmStatic
      fun forChar(c: Char): Level {
        return entries.find { it.char == c } ?: INFO
      }
    }
  }

  companion object {
    const val MSG_SEPARATOR = " "
  }

  fun v(tag: String, msg: String)
  fun v(tag: String, msg: String, tr: Throwable?)
  fun d(tag: String, msg: String)
  fun d(tag: String, msg: String, tr: Throwable?)
  fun i(tag: String, msg: String)
  fun i(tag: String, msg: String, tr: Throwable?)
  fun w(tag: String, msg: String)
  fun w(tag: String, msg: String, tr: Throwable?)
  fun e(tag: String, msg: String)
  fun e(tag: String, msg: String, tr: Throwable?)

  fun generateMessage(vararg args: Any?): String {
    return args.joinToString(separator = MSG_SEPARATOR)
  }
}

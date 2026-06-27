package com.neo.ide.utils

import java.io.PrintWriter
import java.io.StringWriter

object LogUtils {

  @JvmStatic
  fun getFullStackTrace(tr: Throwable): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    tr.printStackTrace(pw)
    return sw.toString()
  }
}

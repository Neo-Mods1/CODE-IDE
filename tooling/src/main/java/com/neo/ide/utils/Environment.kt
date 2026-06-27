package com.neo.ide.utils

import java.io.File

object Environment {

  val HOME: File by lazy {
    File(System.getProperty("user.home") ?: "/data/data/com.termux/files/home")
  }

  val JAVA: File by lazy {
    File(System.getProperty("java.home") ?: "/usr").resolve("bin").resolve("java")
  }

  val TMP_DIR: File by lazy {
    File(HOME, ".ide/tmp").also { it.mkdirs() }
  }

  val TOOLING_API_JAR: File by lazy {
    File(HOME, ".ide/tooling-api.jar")
  }

  val AAPT2: File by lazy {
    File(HOME, ".ide/aapt2")
  }

  val INIT_SCRIPT: File by lazy {
    File(HOME, ".ide/init.gradle").also {
      if (!it.exists()) {
        it.parentFile?.mkdirs()
        it.writeText("")
      }
    }
  }

  @JvmStatic
  fun mkdirIfNotExits(dir: File) {
    if (!dir.exists()) {
      dir.mkdirs()
    }
  }
}

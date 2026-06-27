package com.neo.ide.tasks

import kotlinx.coroutines.CancellationException
import java.util.concurrent.CancellationException as JavaCancellationException

fun Throwable.ifCancelledOrInterrupted(suppress: Boolean = false, block: () -> Unit) {
  if (this is CancellationException || this is JavaCancellationException || this is InterruptedException) {
    if (!suppress) {
      block()
    }
  }
}

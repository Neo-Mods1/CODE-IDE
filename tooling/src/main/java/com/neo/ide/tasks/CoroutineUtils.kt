package com.neo.ide.tasks

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

fun CoroutineScope.cancelIfActive(message: String = "Cancelled") {
  val job = this[Job]
  if (job?.isActive == true) {
    cancel(kotlinx.coroutines.CancellationException(message))
  }
}

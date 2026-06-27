package com.neo.ide.progress

interface ICancelChecker {
  fun isCancelled(): Boolean
  fun cancel()

  class Default : ICancelChecker {
    @Volatile
    private var cancelled = false

    override fun isCancelled(): Boolean = cancelled

    override fun cancel() {
      cancelled = true
    }
  }
}

package com.neo.ide.eventbus.events

open class Event {

  private val data = mutableMapOf<Class<*>, Any?>()

  fun <T> put(key: Class<T>, value: T?) {
    data[key] = value
  }

  @Suppress("UNCHECKED_CAST")
  fun <T> get(key: Class<T>): T? {
    return data[key] as? T
  }
}

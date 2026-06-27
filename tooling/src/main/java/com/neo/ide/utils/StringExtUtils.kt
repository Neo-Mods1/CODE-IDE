package com.neo.ide.utils

fun String.capitalizeString(): String {
  return if (isEmpty()) this else this[0].uppercaseChar() + substring(1)
}

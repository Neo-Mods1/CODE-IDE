package com.itsaky.androidide.utils

import androidx.collection.mutableIntObjectMapOf

fun intByteMapOf(vararg pairs: Pair<Int, Byte>) = mutableIntObjectMapOf<Byte>().apply {
  pairs.forEach { pair -> put(pair.first, pair.second) }
}

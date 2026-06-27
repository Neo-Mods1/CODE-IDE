package com.neo.ide.utils

import java.io.Serializable
import java.util.regex.Pattern

class AndroidPluginVersion private constructor(
  val major: Int,
  val minor: Int,
  val patch: Int,
  val revision: String = ""
) : Comparable<AndroidPluginVersion>, Serializable {

  override fun compareTo(other: AndroidPluginVersion): Int {
    if (major != other.major) return major - other.major
    if (minor != other.minor) return minor - other.minor
    if (patch != other.patch) return patch - other.patch
    return revision.compareTo(other.revision)
  }

  override fun toString(): String {
    val base = "$major.$minor.$patch"
    return if (revision.isNotEmpty()) "$base-$revision" else base
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is AndroidPluginVersion) return false
    return major == other.major && minor == other.minor && patch == other.patch && revision == other.revision
  }

  override fun hashCode(): Int {
    var result = major
    result = 31 * result + minor
    result = 31 * result + patch
    result = 31 * result + revision.hashCode()
    return result
  }

  companion object {
    @JvmField
    val LATEST_TESTED = parse("8.1.0")

    @JvmField
    val MINIMUM_SUPPORTED = parse("7.0.0")

    private val PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)(?:-(.*))?")

    @JvmStatic
    fun parse(version: String): AndroidPluginVersion {
      val matcher = PATTERN.matcher(version)
      if (!matcher.matches()) {
        throw IllegalArgumentException("Invalid version: $version")
      }
      return AndroidPluginVersion(
        major = matcher.group(1)!!.toInt(),
        minor = matcher.group(2)!!.toInt(),
        patch = matcher.group(3)!!.toInt(),
        revision = matcher.group(4) ?: ""
      )
    }
  }
}

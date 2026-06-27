package com.neo.ide.xml.versions

class ApiVersions(
  val platform: File,
  val versions: Map<String, Int> = emptyMap()
) {
  fun getApiVersion(packageName: String): Int {
    return versions[packageName] ?: 0
  }
}

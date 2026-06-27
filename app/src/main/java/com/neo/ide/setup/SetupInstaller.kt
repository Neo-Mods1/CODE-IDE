package com.neo.ide.setup

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit
import java.util.zip.ZipInputStream

class SetupInstaller(private val context: Context) {

    companion object {
        private const val TAG = "SetupInstaller"
        private const val BUFFER_SIZE = 8192
        private const val MANIFEST_URL =
            "https://raw.githubusercontent.com/Neo-Mods1/CODE-IDE-resources/main/manifest.json"
        private const val MANIFEST_VERSION = "v1"
    }

    data class ResourceEntry(
        val tag: String,
        val category: String,
        val name: String,
        val version: String,
        val size: Long,
        val sha256: String,
        val format: String,
        val url: String,
        val destination: String
    )

    data class SetupProgress(
        val step: String,
        val message: String,
        val progress: Float = -1f,
        val isComplete: Boolean = false,
        val isError: Boolean = false
    )

    interface SetupListener {
        fun onProgress(progress: SetupProgress)
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60L, TimeUnit.SECONDS)
        .readTimeout(300L, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    private val prefs by lazy {
        context.getSharedPreferences("setup_state", Context.MODE_PRIVATE)
    }

    suspend fun runSetup(listener: SetupListener) = withContext(Dispatchers.IO) {
        try {
            val homeDir = File(context.filesDir, "home")
            val prefixDir = File(context.filesDir, "usr")
            ensureDirectories(homeDir, prefixDir)

            listener.onProgress(SetupProgress("init", "Fetching manifest..."))
            val resources = fetchManifest(listener)

            if (resources.isEmpty()) {
                listener.onProgress(SetupProgress("error", "No resources found in manifest", isError = true))
                return@withContext
            }

            // Install JDK first
            val jdk = resources.firstOrNull { it.category == "jdk" }
            if (jdk != null) {
                listener.onProgress(SetupProgress("jdk", "Installing ${jdk.name}..."))
                installResource(jdk, prefixDir, listener, "jdk")
            }

            // Install cmdline-tools
            val cmdlineTools = resources.firstOrNull { it.category == "cmdline_tools" }
            if (cmdlineTools != null) {
                listener.onProgress(SetupProgress("sdk", "Installing ${cmdlineTools.name}..."))
                installResource(cmdlineTools, File(homeDir, "android-sdk"), listener, "sdk")
            }

            // Install platform-tools
            val platformTools = resources.firstOrNull { it.category == "platform_tools" }
            if (platformTools != null) {
                listener.onProgress(SetupProgress("platform-tools", "Installing ${platformTools.name}..."))
                installResource(platformTools, File(homeDir, "android-sdk"), listener, "platform-tools")
            }

            // Install build-tools
            val buildTools = resources.firstOrNull { it.category == "build_tools" }
            if (buildTools != null) {
                listener.onProgress(SetupProgress("build-tools", "Installing ${buildTools.name}..."))
                installResource(buildTools, File(homeDir, "android-sdk"), listener, "build-tools")
            }

            // Install platforms
            val platform = resources.firstOrNull { it.category == "platforms" }
            if (platform != null) {
                listener.onProgress(SetupProgress("platform", "Installing ${platform.name}..."))
                installResource(platform, File(homeDir, "android-sdk"), listener, "platform")
            }

            // Install licenses
            val licenses = resources.firstOrNull { it.category == "licenses" }
            if (licenses != null) {
                listener.onProgress(SetupProgress("licenses", "Installing SDK licenses..."))
                installResource(licenses, File(homeDir, "android-sdk"), listener, "licenses")
            }

            prefs.edit().putBoolean("setup_complete", true).apply()
            listener.onProgress(SetupProgress("done", "Setup complete!", isComplete = true))
        } catch (e: Exception) {
            Log.e(TAG, "Setup failed", e)
            listener.onProgress(SetupProgress("error", "Setup failed: ${e.message}", isError = true))
        }
    }

    private fun fetchManifest(listener: SetupListener): List<ResourceEntry> {
        val request = Request.Builder().url(MANIFEST_URL).build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("Failed to fetch manifest: HTTP ${response.code}")
        }

        val body = response.body?.string() ?: throw Exception("Empty manifest body")
        val json = JSONObject(body)

        val versions = json.optJSONObject("versions") ?: throw Exception("No versions block")
        val block = versions.optJSONObject(MANIFEST_VERSION)
            ?: throw Exception("Version $MANIFEST_VERSION not found")

        val resources = mutableListOf<ResourceEntry>()
        val arr = block.optJSONArray("resources") ?: return emptyList()

        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            resources.add(
                ResourceEntry(
                    tag = obj.optString("tag"),
                    category = obj.optString("category"),
                    name = obj.optString("name"),
                    version = obj.optString("version"),
                    size = obj.optLong("size", 0),
                    sha256 = obj.optString("sha256", ""),
                    format = obj.optString("format", "tar.xz"),
                    url = obj.optString("url"),
                    destination = obj.optString("destination")
                )
            )
        }

        listener.onProgress(SetupProgress("manifest", "Found ${resources.size} resources"))
        return resources
    }

    private fun installResource(
        resource: ResourceEntry,
        baseDir: File,
        listener: SetupListener,
        step: String
    ) {
        val destPath = resource.destination.replace("{install_dir}", baseDir.absolutePath)
        val destDir = File(destPath)

        // Skip if already installed
        if (destDir.exists() && destDir.listFiles()?.isNotEmpty() == true) {
            listener.onProgress(SetupProgress(step, "${resource.name} already installed, skipping"))
            return
        }

        if (resource.url.isEmpty()) {
            // Local resource (like licenses) — generate inline
            if (resource.tag == "sdk-licenses") {
                generateLicenses(destDir)
                listener.onProgress(SetupProgress(step, "SDK licenses installed"))
            }
            return
        }

        val cacheDir = File(context.filesDir, "cache")
        cacheDir.mkdirs()
        val archiveFile = File(cacheDir, "${resource.tag}.tar.xz")

        // Download
        downloadFile(resource.url, archiveFile, listener, step)

        // Extract
        listener.onProgress(SetupProgress(step, "Extracting ${resource.name}..."))
        destDir.mkdirs()
        extractTarXz(archiveFile, destDir)
        archiveFile.delete()

        listener.onProgress(SetupProgress(step, "${resource.name} installed"))
    }

    private fun downloadFile(url: String, destFile: File, listener: SetupListener, step: String) {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code}: ${response.message}")
        }

        val body = response.body ?: throw Exception("Empty response body")
        val contentLength = body.contentLength()

        FileOutputStream(destFile).use { fos ->
            BufferedInputStream(body.byteStream(), BUFFER_SIZE).use { input ->
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                var totalRead = 0L
                var lastProgressTime = System.currentTimeMillis()

                while (input.read(buffer).also { bytesRead = it } != -1) {
                    fos.write(buffer, 0, bytesRead)
                    totalRead += bytesRead

                    val now = System.currentTimeMillis()
                    if (now - lastProgressTime >= 300L) {
                        lastProgressTime = now
                        val progress = if (contentLength > 0) totalRead.toFloat() / contentLength else -1f
                        val mb = totalRead / (1024 * 1024)
                        val totalMb = if (contentLength > 0) contentLength / (1024 * 1024) else 0
                        listener.onProgress(
                            SetupProgress(step, "Downloading... ${mb}MB / ${totalMb}MB", progress = progress)
                        )
                    }
                }
            }
        }
    }

    private fun extractTarXz(archive: File, destDir: File) {
        archive.inputStream().buffered().use { fileInput ->
            XZCompressorInputStream(fileInput).use { xzInput ->
                TarArchiveInputStream(xzInput).use { tarInput ->
                    var entry: TarArchiveEntry? = tarInput.nextTarEntry
                    while (entry != null) {
                        if (!entry.isDirectory) {
                            val outFile = File(destDir, entry.name)
                            outFile.parentFile?.mkdirs()
                            FileOutputStream(outFile).use { fos ->
                                tarInput.copyTo(fos, BUFFER_SIZE)
                            }
                            if (entry.mode and 0x49 != 0) {
                                outFile.setExecutable(true, false)
                            }
                        }
                        entry = tarInput.nextTarEntry
                    }
                }
            }
        }
    }

    private fun generateLicenses(destDir: File) {
        destDir.mkdirs()
        val licenses = mapOf(
            "android-sdk-license" to "\nd56f5187479451eabf01fb78af6dfcb131a6481e\n24333f8a63b6825ea9c5514f83c2829b004d1fee",
            "android-sdk-arm-dbt-license" to "\n859f317696f67ef3d7f30a50a5560e7834b43903",
            "android-googletv-license" to "\n601085b94cd77f0b54ff86406957099edd79c4d6",
            "android-sdk-preview-license" to "\n84831b9409646a918e30573bab4c9c91346d8abd",
            "intel-android-extra-license" to "\nd975f751698a77b662f1254ddbeed3901e976f5a"
        )
        for ((name, content) in licenses) {
            File(destDir, name).writeText(content + "\n")
        }
    }

    private fun ensureDirectories(homeDir: File, prefixDir: File) {
        listOf(
            homeDir,
            File(homeDir, "tmp"),
            File(homeDir, ".cache"),
            File(homeDir, "android-sdk"),
            File(prefixDir, "bin"),
            File(prefixDir, "opt"),
            File(prefixDir, "etc"),
            File(context.filesDir, "cache")
        ).forEach { it.mkdirs() }
    }
}

/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.download

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class ResourceManager(private val context: Context) {

    companion object {
        private const val TAG = "ResourceManager"
        const val MANIFEST_URL = "https://raw.githubusercontent.com/Neo-Mods1/CODE-IDE-resources/main/manifest.json"
        private const val PREFS_NAME = "resource_manager"
        private const val KEY_MANIFEST_VERSION = "manifest_version"
    }

    data class ResourceEntry(
        val name: String,
        val category: String,
        val version: String,
        val size: Long,
        val sha256: String,
        val format: String,
        val url: String,
        val destination: String
    )

    data class Manifest(
        val version: String,
        val generated: String,
        val resources: List<ResourceEntry>,
        val categories: Map<String, String>
    )

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val downloader = ResumableDownloader(context)

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getInstallDir(): File {
        return context.filesDir
    }

    suspend fun fetchManifest(): Result<Manifest> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(MANIFEST_URL).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}"))
            }
            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty body"))
            val json = JSONObject(body)

            val categories = mutableMapOf<String, String>()
            val catsObj = json.optJSONObject("categories")
            if (catsObj != null) {
                for (key in catsObj.keys()) {
                    categories[key] = catsObj.getString(key)
                }
            }

            val resources = mutableListOf<ResourceEntry>()
            val resourcesArray = json.getJSONArray("resources")
            for (i in 0 until resourcesArray.length()) {
                val obj = resourcesArray.getJSONObject(i)
                resources.add(
                    ResourceEntry(
                        name = obj.optString("name", obj.optString("label", "unknown")),
                        category = obj.optString("category", "unknown"),
                        version = obj.optString("version", ""),
                        size = obj.optLong("size", 0),
                        sha256 = obj.optString("sha256", ""),
                        format = obj.optString("format", "tar.xz"),
                        url = obj.optString("url", ""),
                        destination = obj.optString("destination", "{install_dir}")
                    )
                )
            }

            Result.success(
                Manifest(
                    version = json.optString("version", "1.0"),
                    generated = json.optString("generated", ""),
                    resources = resources,
                    categories = categories
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch manifest", e)
            Result.failure(e)
        }
    }

    fun isResourceInstalled(resource: ResourceEntry): Boolean {
        val installDir = getInstallDir()
        val destPath = resource.destination.replace("{install_dir}", installDir.absolutePath)
        val destDir = File(destPath)

        if (resource.name == "licenses" || resource.name.contains("license")) {
            return File(installDir, "licenses").exists()
        }

        return destDir.exists() && destDir.listFiles()?.isNotEmpty() == true
    }

    fun getInstalledVersion(): String? {
        return prefs.getString(KEY_MANIFEST_VERSION, null)
    }

    fun setInstalledVersion(version: String) {
        prefs.edit().putString(KEY_MANIFEST_VERSION, version).apply()
    }

    fun getRequiredResources(manifest: Manifest, vararg categories: String): List<ResourceEntry> {
        return manifest.resources.filter { resource ->
            categories.isEmpty() || categories.contains(resource.category)
        }.filter { !isResourceInstalled(it) }
    }

    suspend fun downloadResource(
        resource: ResourceEntry,
        listener: ResumableDownloader.DownloadListener? = null
    ): Result<File> {
        val installDir = getInstallDir()
        val destFile = File(installDir, resource.name + ".tar.xz")

        return downloader.download(
            url = resource.url,
            destination = destFile.absolutePath,
            expectedSha256 = null,
            listener = listener
        )
    }

    suspend fun downloadResources(
        resources: List<ResourceEntry>,
        onResourceStart: ((ResourceEntry, Int, Int) -> Unit)? = null,
        onResourceProgress: ((ResourceEntry, Float) -> Unit)? = null,
        onResourceComplete: ((ResourceEntry) -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ): Boolean {
        if (resources.isEmpty()) return true

        resources.forEachIndexed { index, resource ->
            onResourceStart?.invoke(resource, index + 1, resources.size)

            val result = downloadResource(resource, object : ResumableDownloader.DownloadListener {
                override fun onProgress(state: ResumableDownloader.DownloadState) {
                    onResourceProgress?.invoke(resource, state.progress)
                }

                override fun onComplete(file: File) {
                    onResourceComplete?.invoke(resource)
                }

                override fun onError(error: String) {
                    onError?.invoke("Failed to download ${resource.name}: $error")
                }
            })

            if (result.isFailure) {
                onError?.invoke("Failed to download ${resource.name}")
                return false
            }
        }

        return true
    }

    fun extractResource(resource: ResourceEntry, listener: ExtractionListener? = null): Result<File> {
        val installDir = getInstallDir()
        val archiveFile = File(installDir, resource.name + ".tar.xz")

        if (!archiveFile.exists()) {
            return Result.failure(Exception("Archive not found: ${archiveFile.absolutePath}"))
        }

        listener?.onExtractionStart(archiveFile.name)

        return try {
            when (resource.format) {
                "tar.xz" -> extractTarXz(archiveFile, installDir, resource, listener)
                "zip" -> extractZip(archiveFile, installDir, resource, listener)
                else -> Result.failure(Exception("Unsupported format: ${resource.format}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractTarXz(archive: File, destDir: File, resource: ResourceEntry, listener: ExtractionListener?): Result<File> {
        val process = ProcessBuilder(
            "tar", "xJf", archive.absolutePath, "-C", destDir.absolutePath
        ).redirectErrorStream(true).start()

        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            val error = "tar extraction failed (exit $exitCode): $output"
            listener?.onExtractionError(error)
            return Result.failure(Exception(error))
        }

        archive.delete()

        val destPath = resource.destination.replace("{install_dir}", destDir.absolutePath)
        val destFile = File(destPath)
        listener?.onExtractionComplete(destFile)
        return Result.success(destFile)
    }

    private fun extractZip(archive: File, destDir: File, resource: ResourceEntry, listener: ExtractionListener?): Result<File> {
        val process = ProcessBuilder(
            "unzip", "-o", archive.absolutePath, "-d", destDir.absolutePath
        ).redirectErrorStream(true).start()

        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            val error = "unzip failed (exit $exitCode): $output"
            listener?.onExtractionError(error)
            return Result.failure(Exception(error))
        }

        archive.delete()

        val destPath = resource.destination.replace("{install_dir}", destDir.absolutePath)
        val destFile = File(destPath)
        listener?.onExtractionComplete(destFile)
        return Result.success(destFile)
    }

    interface ExtractionListener {
        fun onExtractionStart(fileName: String)
        fun onExtractionProgress(progress: Float) {}
        fun onExtractionComplete(destDir: File)
        fun onExtractionError(error: String)
    }
}

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
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

class ResourceManager(private val context: Context) {

    companion object {
        private const val TAG = "ResourceManager"
        const val MANIFEST_URL = "https://raw.githubusercontent.com/Neo-Mods1/CODE-IDE-resources/main/manifest.json"
        private const val PREFS_NAME = "resource_manager"
        private const val KEY_MANIFEST_VERSION = "manifest_version"
        private const val COPY_BUFFER_SIZE = 8192
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
        return File(context.filesDir, "home")
    }

    fun getCacheDir(): File {
        val cacheDir = File(getInstallDir(), ".cache")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        return cacheDir
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
                val name = obj.optString("name", obj.optString("label", "unknown"))
                val format = obj.optString("format", "tar.xz")
                resources.add(
                    ResourceEntry(
                        name = name,
                        category = obj.optString("category", "unknown"),
                        version = obj.optString("version", ""),
                        size = obj.optLong("size", 0),
                        sha256 = obj.optString("sha256", ""),
                        format = format,
                        url = obj.optString("url", ""),
                        destination = obj.optString("destination", "{install_dir}/$name")
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

        // Special handling for licenses
        if (resource.name == "licenses" || resource.name.contains("license")) {
            return File(installDir, "licenses").exists()
        }

        // Check if destination directory exists and has files
        if (destDir.exists() && destDir.listFiles()?.isNotEmpty() == true) {
            return true
        }

        // Also check if archive exists in cache (means it was downloaded but not extracted yet)
        val cacheDir = getCacheDir()
        val archiveName = "${resource.name.replace(" ", "_")}.tar.xz"
        val archiveFile = File(cacheDir, archiveName)
        if (archiveFile.exists() && archiveFile.length() > 0) {
            return false // Archive exists but not extracted, need to extract
        }

        return false
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
        val cacheDir = getCacheDir()
        val archiveName = "${resource.name.replace(" ", "_")}.tar.xz"
        val destFile = File(cacheDir, archiveName)

        return downloader.download(
            url = resource.url,
            destination = destFile.absolutePath,
            expectedSha256 = resource.sha256.takeIf { it.isNotEmpty() },
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
        val cacheDir = getCacheDir()
        val archiveName = "${resource.name.replace(" ", "_")}.tar.xz"
        val archiveFile = File(cacheDir, archiveName)

        if (!archiveFile.exists()) {
            return Result.failure(Exception("Archive not found: ${archiveFile.absolutePath}"))
        }

        listener?.onExtractionStart(archiveFile.name)

        return try {
            when {
                resource.format == "tar.xz" || resource.format == "tar" -> {
                    extractTarXz(archiveFile, installDir, resource, listener)
                }
                resource.format == "zip" -> {
                    extractZip(archiveFile, installDir, resource, listener)
                }
                else -> Result.failure(Exception("Unsupported format: ${resource.format}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Extraction failed for ${resource.name}", e)
            listener?.onExtractionError(e.message ?: "Extraction failed")
            Result.failure(e)
        }
    }

    private fun extractTarXz(
        archive: File,
        installDir: File,
        resource: ResourceEntry,
        listener: ExtractionListener?
    ): Result<File> {
        val destPath = resource.destination.replace("{install_dir}", installDir.absolutePath)
        val destDir = File(destPath)
        if (!destDir.exists()) destDir.mkdirs()

        archive.inputStream().buffered().use { fileInput ->
            XZCompressorInputStream(fileInput).use { xzInput ->
                TarArchiveInputStream(xzInput).use { tarInput ->
                    var entry: TarArchiveEntry? = tarInput.nextTarEntry
                    while (entry != null) {
                        if (!entry.isDirectory) {
                            val outFile = File(destDir, entry.name)
                            outFile.parentFile?.mkdirs()
                            FileOutputStream(outFile).use { fos ->
                                tarInput.copyTo(fos, COPY_BUFFER_SIZE)
                            }
                            // Preserve executable permission
                            if (entry.mode and 0x49 != 0) {
                                outFile.setExecutable(true, false)
                            }
                        }
                        entry = tarInput.nextTarEntry
                    }
                }
            }
        }

        // Delete archive after successful extraction
        archive.delete()

        listener?.onExtractionComplete(destDir)
        return Result.success(destDir)
    }

    private fun extractZip(
        archive: File,
        installDir: File,
        resource: ResourceEntry,
        listener: ExtractionListener?
    ): Result<File> {
        val destPath = resource.destination.replace("{install_dir}", installDir.absolutePath)
        val destDir = File(destPath)
        if (!destDir.exists()) destDir.mkdirs()

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

        listener?.onExtractionComplete(destDir)
        return Result.success(destDir)
    }

    interface ExtractionListener {
        fun onExtractionStart(fileName: String)
        fun onExtractionProgress(progress: Float) {}
        fun onExtractionComplete(destDir: File)
        fun onExtractionError(error: String)
    }
}

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║                    CODE-IDE • NeoMods                      ║
 * ║                  Advanced Android IDE Project              ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 *  (っ◔◡◔)っ ♥
 *
 *  Developer         • NeoMods
 *  Telegram Contact  • @NeoModsDev
 *  Telegram Channel  • https://t.me/NeoModsChannel
 *
 * ──────────────────────────────────────────────────────────────
 *  PROJECT NOTICE
 * ──────────────────────────────────────────────────────────────
 *
 *  This source file is part of the CODE-IDE project.
 *
 *  Unauthorized copying, extraction, redistribution,
 *  mirroring, downloading, modification, or reuse of
 *  CODE-IDE source files is NOT permitted without
 *  explicit permission from the developer.
 *
 *  The application may expose certain components in
 *  read-only mode for educational or preview purposes,
 *  however this DOES NOT grant permission to reuse
 *  or redistribute the source code.
 *
 *  If you need access to the original source code,
 *  implementation details, licensing, or collaboration,
 *  please contact the developer directly.
 *
 *  © NeoMods — All Rights Reserved
 * ──────────────────────────────────────────────────────────────
 */

package com.neo.ide.download

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.util.concurrent.TimeUnit

class ResumableDownloader(private val context: Context) {

    companion object {
        private const val TAG = "ResumableDownloader"
        private const val BUFFER_SIZE = 8192
    }

    data class DownloadState(
        val url: String,
        val destination: String,
        val bytesDownloaded: Long = 0,
        val totalBytes: Long = 0,
        val isComplete: Boolean = false,
        val error: String? = null
    ) {
        val progress: Float
            get() = if (totalBytes > 0) bytesDownloaded.toFloat() / totalBytes else 0f
    }

    interface DownloadListener {
        fun onProgress(state: DownloadState)
        fun onComplete(file: File)
        fun onError(error: String)
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30L, TimeUnit.SECONDS)
        .readTimeout(120L, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    /**
     * Get the download cache directory: ~/.cache/
     */
    private fun getCacheDir(): File {
        val homeDir = File(context.filesDir, "home")
        val cacheDir = File(homeDir, ".cache")
        cacheDir.mkdirs()
        return cacheDir
    }

    suspend fun download(
        url: String,
        destination: String,
        expectedSha256: String? = null,
        listener: DownloadListener? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        val destFile = File(destination)
        destFile.parentFile?.mkdirs()

        try {
            val result = doDownload(url, destFile, expectedSha256, listener)
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "Download failed: ${e.message}")
            listener?.onError(e.message ?: "Download failed")
            Result.failure(e)
        }
    }

    private fun doDownload(
        url: String,
        destFile: File,
        expectedSha256: String?,
        listener: DownloadListener?
    ): File {
        val partialFile = File(destFile.parent, "${destFile.name}.partial")
        val stateFile = File(destFile.parent, "${destFile.name}.state")

        // Resume state: bytes downloaded so far
        var existingBytes = 0L
        if (partialFile.exists()) {
            existingBytes = partialFile.length()
        }

        val requestBuilder = Request.Builder().url(url)
        if (existingBytes > 0) {
            requestBuilder.addHeader("Range", "bytes=${existingBytes}-")
            Log.d(TAG, "Resuming download from byte $existingBytes")
        }

        val response = client.newCall(requestBuilder.build()).execute()

        if (response.code == 416) {
            // Range not satisfiable — file already complete
            if (partialFile.exists()) {
                partialFile.renameTo(destFile)
            }
            listener?.onComplete(destFile)
            return destFile
        }

        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code}: ${response.message}")
        }

        val body = response.body ?: throw Exception("Empty response body")
        val contentLength = body.contentLength()
        val isResuming = response.code == 206

        val totalBytes = if (isResuming) existingBytes + contentLength else contentLength

        val appendMode = isResuming && existingBytes > 0
        val fos = if (appendMode) {
            FileOutputStream(partialFile, true) // append mode
        } else {
            if (partialFile.exists()) partialFile.delete()
            FileOutputStream(partialFile)
        }

        fos.use { out ->
            BufferedInputStream(body.byteStream(), BUFFER_SIZE).use { input ->
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                var totalRead = existingBytes
                var lastProgressTime = System.currentTimeMillis()

                while (input.read(buffer).also { bytesRead = it } != -1) {
                    out.write(buffer, 0, bytesRead)
                    totalRead += bytesRead

                    val now = System.currentTimeMillis()
                    if (now - lastProgressTime >= 250L) {
                        lastProgressTime = now
                        listener?.onProgress(
                            DownloadState(url, destFile.absolutePath, totalRead, totalBytes)
                        )
                    }
                }
            }
        }

        // SHA-256 verification if provided
        if (!expectedSha256.isNullOrEmpty()) {
            val actualSha256 = computeSha256(partialFile)
            if (!actualSha256.equals(expectedSha256, ignoreCase = true)) {
                partialFile.delete()
                throw Exception("SHA-256 mismatch: expected=$expectedSha256 actual=$actualSha256")
            }
        }

        // Move partial to final
        if (destFile.exists()) destFile.delete()
        partialFile.renameTo(destFile)

        // Clean up state file
        stateFile.delete()

        listener?.onComplete(destFile)
        return destFile
    }

    private fun computeSha256(file: File): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        BufferedInputStream(file.inputStream(), BUFFER_SIZE).use { input ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}

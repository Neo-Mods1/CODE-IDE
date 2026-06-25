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

    suspend fun download(
        url: String,
        destination: String,
        expectedSha256: String? = null,
        listener: DownloadListener? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        val destFile = File(destination)
        destFile.parentFile?.mkdirs()

        // Delete any leftover state files from old ResumableDownloader
        try { File("$destination.state").delete() } catch (_: Exception) {}

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
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("HTTP ${response.code}: ${response.message}")
        }

        val body = response.body ?: throw Exception("Empty response body")
        val contentLength = body.contentLength()

        // Always download fresh - no resume
        if (destFile.exists()) destFile.delete()

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
                    if (now - lastProgressTime >= 250L) {
                        lastProgressTime = now
                        listener?.onProgress(
                            DownloadState(url, destFile.absolutePath, totalRead, contentLength)
                        )
                    }
                }
            }
        }

        // SHA-256 verification if provided
        if (!expectedSha256.isNullOrEmpty()) {
            val actualSha256 = computeSha256(destFile)
            if (!actualSha256.equals(expectedSha256, ignoreCase = true)) {
                destFile.delete()
                throw Exception("SHA-256 mismatch: expected=$expectedSha256 actual=$actualSha256")
            }
        }

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

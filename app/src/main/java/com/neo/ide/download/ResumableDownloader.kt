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
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

class ResumableDownloader(private val context: Context) {

    companion object {
        private const val TAG = "ResumableDownloader"
        private const val MAX_RETRIES = 3
        private const val CONNECT_TIMEOUT = 30L
        private const val READ_TIMEOUT = 120L

        // 4MB read buffer — big chunks = fewer syscalls = fast throughput
        private const val CHUNK_SIZE = 4 * 1024 * 1024

        // 8MB write buffer — absorbs bursts, reduces disk stalls
        private const val WRITE_BUFFER_SIZE = 8 * 1024 * 1024

        // Save state every 32MB — not every 3MB, reduces mid-loop disk writes
        private const val STATE_SAVE_INTERVAL = 32 * 1024 * 1024L

        // Throttle progress callbacks to every 250ms — UI overhead was killing throughput
        private const val PROGRESS_INTERVAL_MS = 250L
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .connectionPool(ConnectionPool(5, 2, TimeUnit.MINUTES))
        .build()

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

    private fun getStateFile(destination: String): File {
        return File("$destination.state")
    }

    private fun saveState(state: DownloadState) {
        try {
            val stateFile = getStateFile(state.destination)
            stateFile.writeText("${state.url}\n${state.bytesDownloaded}\n${state.totalBytes}")
        } catch (_: Exception) {}
    }

    private fun loadState(destination: String): DownloadState? {
        val stateFile = getStateFile(destination)
        if (!stateFile.exists()) return null
        return try {
            val lines = stateFile.readLines()
            if (lines.size < 3) return null
            DownloadState(
                url = lines[0],
                destination = destination,
                bytesDownloaded = lines[1].toLong(),
                totalBytes = lines[2].toLong()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun clearState(destination: String) {
        try { getStateFile(destination).delete() } catch (_: Exception) {}
    }

    suspend fun download(
        url: String,
        destination: String,
        expectedSha256: String? = null,
        listener: DownloadListener? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        val destFile = File(destination)
        destFile.parentFile?.mkdirs()

        var retries = 0
        while (retries < MAX_RETRIES) {
            try {
                return@withContext Result.success(doDownload(url, destFile, expectedSha256, listener))
            } catch (e: Exception) {
                retries++
                Log.w(TAG, "Download failed (attempt $retries/$MAX_RETRIES): ${e.message}")
                if (retries >= MAX_RETRIES) {
                    listener?.onError(e.message ?: "Download failed")
                    return@withContext Result.failure(e)
                }
                kotlinx.coroutines.delay(1000L * retries)
            }
        }
        return@withContext Result.failure(IOException("Max retries exceeded"))
    }

    private suspend fun doDownload(
        url: String,
        destFile: File,
        expectedSha256: String?,
        listener: DownloadListener?
    ): File {
        val existingState = loadState(destFile.absolutePath)
        var bytesDownloaded = existingState?.bytesDownloaded ?: 0L
        var totalBytes = existingState?.totalBytes ?: 0L

        val requestBuilder = Request.Builder().url(url)
        if (bytesDownloaded > 0) {
            requestBuilder.addHeader("Range", "bytes=$bytesDownloaded-")
            Log.d(TAG, "Resuming download from byte $bytesDownloaded")
        }

        val response = client.newCall(requestBuilder.build()).execute()
        if (!response.isSuccessful && response.code != 206) {
            throw IOException("HTTP ${response.code}: ${response.message}")
        }

        val body = response.body ?: throw IOException("Empty response body")
        val contentLength = body.contentLength()
        if (totalBytes == 0L) {
            totalBytes = if (response.code == 206) {
                bytesDownloaded + contentLength
            } else {
                contentLength.toLong()
            }
        }

        if (response.code == 200) {
            bytesDownloaded = 0L
        }

        val fos = FileOutputStream(destFile, bytesDownloaded > 0)
        val bufferedOut = BufferedOutputStream(fos, WRITE_BUFFER_SIZE)
        val bufferedIn = BufferedInputStream(body.byteStream(), CHUNK_SIZE)

        var lastStateSave = bytesDownloaded
        var lastProgressTime = System.currentTimeMillis()

        try {
            val buffer = ByteArray(CHUNK_SIZE)
            var bytesRead: Int
            while (bufferedIn.read(buffer).also { bytesRead = it } != -1) {
                coroutineContext.ensureActive()
                bufferedOut.write(buffer, 0, bytesRead)
                bytesDownloaded += bytesRead

                val now = System.currentTimeMillis()
                if (now - lastProgressTime >= PROGRESS_INTERVAL_MS) {
                    lastProgressTime = now
                    listener?.onProgress(
                        DownloadState(url, destFile.absolutePath, bytesDownloaded, totalBytes)
                    )
                }

                if (bytesDownloaded - lastStateSave >= STATE_SAVE_INTERVAL) {
                    bufferedOut.flush()
                    saveState(DownloadState(url, destFile.absolutePath, bytesDownloaded, totalBytes))
                    lastStateSave = bytesDownloaded
                }
            }

            bufferedOut.flush()
        } finally {
            bufferedIn.close()
            bufferedOut.close()
        }

        saveState(DownloadState(url, destFile.absolutePath, bytesDownloaded, totalBytes, isComplete = true))

        if (!expectedSha256.isNullOrEmpty()) {
            val actualSha256 = computeSha256(destFile)
            if (!actualSha256.equals(expectedSha256, ignoreCase = true)) {
                destFile.delete()
                clearState(destFile.absolutePath)
                throw IOException("SHA-256 mismatch: expected=$expectedSha256 actual=$actualSha256")
            }
        }

        clearState(destFile.absolutePath)
        listener?.onComplete(destFile)
        return destFile
    }

    fun computeSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        BufferedInputStream(file.inputStream(), CHUNK_SIZE).use { input ->
            val buffer = ByteArray(CHUNK_SIZE)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}

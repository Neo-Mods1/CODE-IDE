/**
 *	(っ◔◡◔)っ ♥
 *
 *	Telegram Contact • @NeoModsDev
 *	Telegram Channel • https://t.me/NeoModsChannel
 */

package com.neo.ide.download

import android.content.Context
import android.util.Log
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.AbstractFetchListener
import com.tonyodev.fetch2.NetworkType
import com.tonyodev.fetch2.Priority
import com.tonyodev.fetch2.Request
import com.tonyodev.fetch2.Status
import com.tonyodev.fetch2.exception.FetchException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.security.MessageDigest

class ResumableDownloader(private val context: Context) {

    companion object {
        private const val TAG = "ResumableDownloader"
        private const val READ_BUFFER_SIZE = 4 * 1024 * 1024
    }

    private val fetch: Fetch by lazy {
        val config = FetchConfiguration.Builder(context)
            .setDownloadConcurrentLimit(3)
            .build()
        Fetch.Impl.getInstance(config)
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

    suspend fun download(
        url: String,
        destination: String,
        expectedSha256: String? = null,
        listener: DownloadListener? = null
    ): Result<File> = withContext(Dispatchers.IO) {
        val destFile = File(destination)
        destFile.parentFile?.mkdirs()

        val deferred = CompletableDeferred<Result<File>>()
        var fetchId = -1

        val fetchListener = object : AbstractFetchListener() {
            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
                fetchId = download.id
            }

            override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
                val total = download.total
                val downloaded = download.downloaded
                listener?.onProgress(
                    DownloadState(
                        url = url,
                        destination = destination,
                        bytesDownloaded = downloaded,
                        totalBytes = total
                    )
                )
            }

            override fun onCompleted(download: Download) {
                fetch.removeListener(this)
                val file = File(download.file)
                if (!expectedSha256.isNullOrEmpty()) {
                    val actualSha256 = computeSha256(file)
                    if (!actualSha256.equals(expectedSha256, ignoreCase = true)) {
                        file.delete()
                        val error = "SHA-256 mismatch: expected=$expectedSha256 actual=$actualSha256"
                        listener?.onError(error)
                        deferred.complete(Result.failure(IOException(error)))
                        return
                    }
                }
                listener?.onComplete(file)
                deferred.complete(Result.success(file))
            }

            override fun onError(download: Download, error: com.tonyodev.fetch2.Error, throwable: Throwable?) {
                fetch.removeListener(this)
                val msg = throwable?.message ?: error.throwable?.message ?: "Download failed"
                Log.e(TAG, "Fetch download error: $msg")
                listener?.onError(msg)
                deferred.complete(Result.failure(IOException(msg)))
            }

            override fun onCancelled(download: Download) {
                fetch.removeListener(this)
                val error = "Download cancelled"
                listener?.onError(error)
                deferred.complete(Result.failure(IOException(error)))
            }
        }

        fetch.addListener(fetchListener)

        try {
            val request = Request(url, destination)
            request.priority = Priority.HIGH
            request.networkType = NetworkType.ALL
            request.autoRetryMaxAttempts = 3

            fetch.enqueue(request,
                { updatedRequest ->
                    fetchId = updatedRequest.id
                },
                { error ->
                    fetch.removeListener(fetchListener)
                    val msg = error.throwable?.message ?: "Failed to enqueue download"
                    Log.e(TAG, "Fetch enqueue error: $msg")
                    listener?.onError(msg)
                    deferred.complete(Result.failure(IOException(msg)))
                }
            )
        } catch (e: FetchException) {
            fetch.removeListener(fetchListener)
            Log.e(TAG, "Fetch exception: ${e.message}")
            listener?.onError(e.message ?: "Fetch error")
            deferred.complete(Result.failure(e))
        }

        deferred.await()
    }

    fun cancelDownload(url: String) {
        fetch.getDownloadsWithStatus(Status.DOWNLOADING) { downloads ->
            for (download in downloads) {
                if (download.url == url) {
                    fetch.cancel(download.id)
                    break
                }
            }
        }
    }

    fun cancelAllDownloads() {
        fetch.cancelAll()
    }

    fun close() {
        fetch.close()
    }

    fun computeSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        BufferedInputStream(file.inputStream(), READ_BUFFER_SIZE).use { input ->
            val buffer = ByteArray(READ_BUFFER_SIZE)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}

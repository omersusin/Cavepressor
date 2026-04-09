package com.cavepressor.domain.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

sealed class DownloadState {
    object Idle : DownloadState()
    data class Downloading(val progress: Int) : DownloadState()
    object Success : DownloadState()
    data class Error(val message: String) : DownloadState()
}

class DownloadLocalModelUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val client = OkHttpClient()

    fun isModelDownloaded(filename: String): Boolean {
        val modelDirectory = File(context.filesDir, "models")
        val targetFile = File(modelDirectory, filename)
        return targetFile.exists() && targetFile.length() > 0
    }

    operator fun invoke(url: String, filename: String): Flow<DownloadState> = flow {
        val modelDirectory = File(context.filesDir, "models").apply { mkdirs() }
        val targetFile = File(modelDirectory, filename)
        val tempFile = File(modelDirectory, "$filename.tmp")

        if (targetFile.exists() && targetFile.length() > 0) {
            emit(DownloadState.Success)
            return@flow
        }

        try {
            emit(DownloadState.Downloading(0))
            
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                emit(DownloadState.Error("Network Error: ${response.code}"))
                return@flow
            }

            val body = response.body ?: throw Exception("Empty response body")
            val totalBytes = body.contentLength()
            var downloadedBytes = 0L

            body.byteStream().use { input ->
                FileOutputStream(tempFile).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int
                    var lastProgress = 0

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead

                        if (totalBytes > 0) {
                            val currentProgress = ((downloadedBytes * 100) / totalBytes).toInt()
                            if (currentProgress > lastProgress) {
                                lastProgress = currentProgress
                                emit(DownloadState.Downloading(currentProgress))
                            }
                        }
                    }
                    output.flush()
                }
            }

            // Move temp file to actual file
            if (tempFile.renameTo(targetFile)) {
                emit(DownloadState.Success)
            } else {
                emit(DownloadState.Error("Failed to save downloaded file"))
                tempFile.delete()
            }
        } catch (e: Exception) {
            tempFile.delete()
            emit(DownloadState.Error(e.message ?: "Unknown download error"))
        }
    }.flowOn(Dispatchers.IO)
}

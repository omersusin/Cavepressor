package com.cavepressor.data.llm

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalLlmService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var llmInference: LlmInference? = null
    private var currentModelPath: String? = null

    val modelDirectory: File
        get() = File(context.filesDir, "models").apply { mkdirs() }

    suspend fun initialize(modelFileName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val modelFile = File(modelDirectory, modelFileName)
            if (!modelFile.exists()) {
                return@withContext Result.failure(Exception("Model file not found. Please download it first."))
            }

            if (llmInference != null && currentModelPath == modelFile.absolutePath) {
                return@withContext Result.success(Unit) // Already initialized
            }

            llmInference?.close()

            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelFile.absolutePath)
                .setMaxTokens(1024)
                .build()

            llmInference = LlmInference.createFromOptions(context, options)
            currentModelPath = modelFile.absolutePath

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateResponse(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val inference = llmInference ?: return@withContext Result.failure(Exception("LLM Not Initialized"))
            val response = inference.generateResponse(prompt)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

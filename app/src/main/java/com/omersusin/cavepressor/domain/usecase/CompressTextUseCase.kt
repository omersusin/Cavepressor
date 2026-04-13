package com.cavepressor.domain.usecase

import com.cavepressor.data.datastore.SettingsDataStore
import com.cavepressor.data.repository.CompressionRepository
import com.cavepressor.di.NetworkModule
import com.cavepressor.network.api.HuggingFaceApi
import com.cavepressor.domain.model.ApiProvider
import com.cavepressor.domain.model.CompressionLevel
import com.cavepressor.domain.model.CompressionResult
import com.cavepressor.network.model.ChatMessage
import com.cavepressor.network.model.ChatRequest
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CompressTextUseCase @Inject constructor(
    private val settings: SettingsDataStore,
    private val repository: CompressionRepository,
    private val moshi: Moshi
) {
    suspend operator fun invoke(text: String): Result<CompressionResult> {
        return try {
            val provider = settings.selectedProvider.first()
            val model = settings.selectedModel.first()
            val level = settings.compressionLevel.first()
            val apiKey = when (provider) {
                ApiProvider.OPENROUTER -> settings.openRouterApiKey.first()
                ApiProvider.GROQ -> settings.groqApiKey.first()
                ApiProvider.HUGGING_FACE -> settings.huggingFaceApiKey.first()
            }

            if (apiKey.isBlank()) {
                return Result.failure(Exception("API key not set for ${provider.displayName}. Go to Settings."))
            }

            if (model.isBlank()) {
                return Result.failure(Exception("No model selected. Go to Settings."))
            }

            val messages = listOf(
                ChatMessage(
                    role = "system",
                    content = buildSystemPrompt(level)
                ),
                ChatMessage(
                    role = "user",
                    content = text.trim()
                )
            )

            val request = ChatRequest(
                model = model.trim(),
                messages = messages,
                temperature = 0.3,
                maxTokens = 2048
            )

            val responseText = when (provider) {
                ApiProvider.OPENROUTER -> {
                    val api = NetworkModule.buildOpenRouterApi(apiKey.trim(), moshi)
                    val response = api.compress(request)
                    response.choices.firstOrNull()?.message?.content
                        ?: return Result.failure(Exception("Empty response from OpenRouter"))
                }
                ApiProvider.GROQ -> {
                    val api = NetworkModule.buildGroqApi(apiKey.trim(), moshi)
                    val response = api.compress(request)
                    response.choices.firstOrNull()?.message?.content
                        ?: return Result.failure(Exception("Empty response from Groq"))
                }
                ApiProvider.HUGGING_FACE -> {
                    val api = NetworkModule.buildHuggingFaceApi(apiKey.trim(), moshi)
                    val response = api.compress(request)
                    response.choices.firstOrNull()?.message?.content
                        ?: return Result.failure(Exception("Empty response from Hugging Face"))
                }
            }

            val originalTokens = estimateTokens(text)
            val compressedTokens = estimateTokens(responseText)
            val reduction = if (originalTokens > 0) {
                if (originalTokens > 0) ((originalTokens - compressedTokens) * 100 / originalTokens) else 0
            } else 0

            val result = CompressionResult(
                originalText = text,
                compressedText = responseText,
                originalTokens = originalTokens,
                compressedTokens = compressedTokens,
                reductionPercent = reduction,
                provider = provider,
                model = model,
                level = level
            )

            val savedId = repository.save(result)
            Result.success(result.copy(id = savedId))

        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "No error body"
            Result.failure(Exception("HTTP ${e.code()}: $errorBody"))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Unknown error"))
        }
    }

    private fun buildSystemPrompt(level: CompressionLevel): String {
        val targetRatio = when (level) {
            CompressionLevel.LIGHT -> "Shorten to 70-85% of original length."
            CompressionLevel.MEDIUM -> "Shorten to 55-70% of original length."
            CompressionLevel.AGGRESSIVE -> "Shorten to 40-55% of original length. Use dense, telegram-style prose."
        }
        return """You are a text summarization tool. Summarize the input text.

Rules (mandatory):
- Output the summarized text ONLY. No preamble, no labels.
- Use the same language as the input text.
- $targetRatio
- Remove filler, repetition, and redundant phrases.
- Do NOT refuse, do NOT explain. Just output the summary."""
    }

    fun estimateTokens(text: String): Int {
        if (text.isBlank()) return 0
        val words = text.trim().split(Regex("\\s+")).size
        val chars = text.length / 4
        return ((words + chars) / 2).coerceAtLeast(1)
    }
}

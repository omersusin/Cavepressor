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
        val intensity = when (level) {
            CompressionLevel.LIGHT -> """Lightly compress the text. Remove only redundant filler words and minor repetitions. Keep about 70-85% of the original length. Preserve all key details."""
            CompressionLevel.MEDIUM -> """Moderately compress the text. Remove redundant phrases, filler words, and non-essential details. Keep about 55-70% of the original length. Preserve main points."""
            CompressionLevel.AGGRESSIVE -> """Aggressively compress the text to its absolute core meaning. Use telegram-style shorthand, remove all fluff. Keep only 40-55% of the original length."""
        }
        return """You are a text compression engine. Compress the following text according to the instruction below.

CRITICAL RULES - follow exactly:
1. Output ONLY the compressed text. No explanations, no labels, no commentary.
2. Keep the EXACT SAME LANGUAGE as the input. If input is Turkish, output Turkish. If input is English, output English. NEVER translate.
3. Do NOT start with phrases like "Here is", "Compressed:", "Result:" etc.
4. Do NOT add arrows (→), translations, or glossaries.
5. Preserve the meaning and tone of the original.

Compression instruction: $intensity"""
    }

    fun estimateTokens(text: String): Int {
        if (text.isBlank()) return 0
        val words = text.trim().split(Regex("\\s+")).size
        val chars = text.length / 4
        return ((words + chars) / 2).coerceAtLeast(1)
    }
}

package com.omersusin.cavepressor.domain.usecase

import com.omersusin.cavepressor.data.datastore.SettingsDataStore
import com.omersusin.cavepressor.data.repository.CompressionRepository
import com.omersusin.cavepressor.di.NetworkModule
import com.omersusin.cavepressor.domain.model.ApiProvider
import com.omersusin.cavepressor.domain.model.CompressionLevel
import com.omersusin.cavepressor.domain.model.CompressionResult
import com.omersusin.cavepressor.network.model.ChatMessage
import com.omersusin.cavepressor.network.model.ChatRequest
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
            }

            val originalTokens = estimateTokens(text)
            val compressedTokens = estimateTokens(responseText)
            val reduction = if (originalTokens > 0) {
                ((originalTokens - compressedTokens) * 100 / originalTokens).coerceIn(0, 99)
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
        return when (level) {
            CompressionLevel.LIGHT ->
                "You are a text compression assistant. Apply light compression (15-30% reduction). " +
                "Remove filler words (very, quite, basically, essentially), unnecessary articles (a, an, the) where meaning is clear. " +
                "Keep sentence structure mostly intact. Preserve ALL facts: numbers, names, dates, technical terms. " +
                "Output ONLY the compressed text, nothing else."

            CompressionLevel.MEDIUM ->
                "You are a Caveman Compression engine. Apply medium compression (30-45% reduction). " +
                "Rules: Remove articles (a, an, the). Remove weak conjunctions (however, therefore, because, in order to). " +
                "Convert passive to active voice. Use short sentences, one idea per sentence. " +
                "Use action verbs: do, make, fix, check, use. " +
                "PRESERVE: all numbers, names, dates, technical terms, constraints, specific details. " +
                "Output ONLY the compressed text, nothing else."

            CompressionLevel.AGGRESSIVE ->
                "You are a Caveman Compression engine. Apply aggressive compression (45-60% reduction). " +
                "Rules: Strip all grammar — articles, conjunctions, prepositions. No passive voice. " +
                "Maximum 4 words per sentence. Telegram style: nouns + verbs + critical adjectives only. " +
                "Use symbols: → instead of leads to, & instead of and, w/ instead of with. " +
                "MUST preserve: numbers, proper nouns, technical terms, constraints. " +
                "Output ONLY the compressed text, nothing else."
        }
    }

    fun estimateTokens(text: String): Int {
        if (text.isBlank()) return 0
        val words = text.trim().split(Regex("\\s+")).size
        val chars = text.length / 4
        return ((words + chars) / 2).coerceAtLeast(1)
    }
}

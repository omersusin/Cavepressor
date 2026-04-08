package com.omersusin.cavepressor.domain.usecase

import com.omersusin.cavepressor.data.datastore.SettingsDataStore
import com.omersusin.cavepressor.data.repository.CompressionRepository
import com.omersusin.cavepressor.di.NetworkModule
import com.omersusin.cavepressor.domain.model.ApiProvider
import com.omersusin.cavepressor.domain.model.CaveModel
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
                return Result.failure(Exception("API key not set for ${provider.displayName}"))
            }

            val systemPrompt = buildSystemPrompt(level)
            val request = ChatRequest(
                model = model,
                messages = listOf(
                    ChatMessage(role = "system", content = systemPrompt),
                    ChatMessage(role = "user", content = text)
                )
            )

            val response = when (provider) {
                ApiProvider.OPENROUTER -> {
                    val api = NetworkModule.buildOpenRouterApi(apiKey, moshi)
                    api.compress(request)
                }
                ApiProvider.GROQ -> {
                    val api = NetworkModule.buildGroqApi(apiKey, moshi)
                    api.compress(request)
                }
            }

            val compressedText = response.choices.firstOrNull()?.message?.content
                ?: return Result.failure(Exception("Empty response from API"))

            val originalTokens = estimateTokens(text)
            val compressedTokens = estimateTokens(compressedText)
            val reduction = if (originalTokens > 0) {
                ((originalTokens - compressedTokens) * 100 / originalTokens).coerceAtLeast(0)
            } else 0

            val result = CompressionResult(
                originalText = text,
                compressedText = compressedText,
                originalTokens = originalTokens,
                compressedTokens = compressedTokens,
                reductionPercent = reduction,
                provider = provider,
                model = model,
                level = level
            )

            val savedId = repository.save(result)
            Result.success(result.copy(id = savedId))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildSystemPrompt(level: CompressionLevel): String {
        val intensity = when (level) {
            CompressionLevel.LIGHT -> """
                Apply LIGHT caveman compression (target 15-30% reduction):
                - Remove only obvious filler words (very, quite, essentially, basically)
                - Remove articles (a, an, the) where meaning is preserved
                - Keep most conjunctions and sentence structure intact
                - Preserve all facts, numbers, names, technical terms
            """.trimIndent()

            CompressionLevel.MEDIUM -> """
                Apply MEDIUM caveman compression (target 30-45% reduction):
                - Remove articles (a, an, the), filler words
                - Remove weak conjunctions (however, therefore, because, in order to)
                - Convert passive voice to active voice
                - Keep 2-5 words per sentence, one atomic thought per sentence
                - Use action verbs: do, make, fix, check, use
                - Preserve ALL facts: numbers, names, dates, technical terms, constraints
            """.trimIndent()

            CompressionLevel.AGGRESSIVE -> """
                Apply AGGRESSIVE caveman compression (target 45-60% reduction):
                - Strip all grammar: articles, conjunctions, prepositions
                - Remove all passive constructions
                - Maximum 3 words per sentence
                - Telegram-style: only nouns, verbs, critical adjectives
                - Use symbols where possible: → instead of "leads to", & instead of "and"
                - MUST preserve: numbers, proper nouns, technical terms, constraints
                - No filler, no decoration, pure information density
            """.trimIndent()
        }

        return """
            You are a Caveman Compression engine. Your job is to compress text while preserving all semantic meaning and facts.
            
            Core rules:
            1. Strip grammar, keep facts
            2. Remove only what LLMs can reliably reconstruct
            3. Never remove: numbers, names, dates, technical terms, specific constraints
            4. Output ONLY the compressed text, no explanations, no preamble
            
            $intensity
        """.trimIndent()
    }

    fun estimateTokens(text: String): Int {
        if (text.isBlank()) return 0
        // GPT-2 tokenizer yaklaşımı: ortalama 4 karakter = 1 token
        val byWhitespace = text.trim().split(Regex("\\s+")).size
        val byChars = text.length / 4
        return ((byWhitespace + byChars) / 2).coerceAtLeast(1)
    }
}

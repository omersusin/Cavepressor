package com.cavepressor.domain.usecase

import com.cavepressor.data.datastore.SettingsDataStore
import com.cavepressor.di.NetworkModule
import com.cavepressor.domain.model.ApiProvider
import com.cavepressor.domain.model.CaveModel
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FetchModelsUseCase @Inject constructor(
    private val settings: SettingsDataStore,
    private val moshi: Moshi
) {
    private val groqModels = listOf(
        CaveModel("llama-3.3-70b-versatile", "Llama 3.3 70B Versatile", ApiProvider.GROQ),
        CaveModel("llama-3.1-70b-versatile", "Llama 3.1 70B Versatile", ApiProvider.GROQ),
        CaveModel("llama-3.1-8b-instant", "Llama 3.1 8B Instant", ApiProvider.GROQ),
        CaveModel("llama3-70b-8192", "Llama 3 70B", ApiProvider.GROQ),
        CaveModel("llama3-8b-8192", "Llama 3 8B", ApiProvider.GROQ),
        CaveModel("mixtral-8x7b-32768", "Mixtral 8x7B", ApiProvider.GROQ),
        CaveModel("gemma2-9b-it", "Gemma 2 9B", ApiProvider.GROQ),
        CaveModel("gemma-7b-it", "Gemma 7B", ApiProvider.GROQ)
    )

    private val openRouterModels = listOf(
        CaveModel("openai/gpt-4o-mini", "GPT-4o Mini", ApiProvider.OPENROUTER),
        CaveModel("openai/gpt-4o", "GPT-4o", ApiProvider.OPENROUTER),
        CaveModel("openai/gpt-4-turbo", "GPT-4 Turbo", ApiProvider.OPENROUTER),
        CaveModel("anthropic/claude-3.5-sonnet", "Claude 3.5 Sonnet", ApiProvider.OPENROUTER),
        CaveModel("anthropic/claude-3.5-haiku", "Claude 3.5 Haiku", ApiProvider.OPENROUTER),
        CaveModel("anthropic/claude-3-haiku", "Claude 3 Haiku", ApiProvider.OPENROUTER),
        CaveModel("google/gemini-flash-1.5", "Gemini 1.5 Flash", ApiProvider.OPENROUTER),
        CaveModel("google/gemini-pro-1.5", "Gemini 1.5 Pro", ApiProvider.OPENROUTER),
        CaveModel("google/gemini-2.0-flash-001", "Gemini 2.0 Flash", ApiProvider.OPENROUTER),
        CaveModel("meta-llama/llama-3.3-70b-instruct", "Llama 3.3 70B", ApiProvider.OPENROUTER),
        CaveModel("meta-llama/llama-3.1-405b-instruct", "Llama 3.1 405B", ApiProvider.OPENROUTER),
        CaveModel("mistralai/mistral-large", "Mistral Large", ApiProvider.OPENROUTER),
        CaveModel("mistralai/mixtral-8x7b-instruct", "Mixtral 8x7B", ApiProvider.OPENROUTER),
        CaveModel("deepseek/deepseek-chat", "DeepSeek V3", ApiProvider.OPENROUTER),
        CaveModel("deepseek/deepseek-r1", "DeepSeek R1", ApiProvider.OPENROUTER)
    )

    private val huggingFaceModels = listOf(
        CaveModel("meta-llama/Llama-3.2-3B-Instruct", "Llama 3.2 3B Instruct", ApiProvider.HUGGING_FACE),
        CaveModel("meta-llama/Meta-Llama-3-8B-Instruct", "Llama 3 8B Instruct", ApiProvider.HUGGING_FACE),
        CaveModel("mistralai/Mistral-7B-Instruct-v0.3", "Mistral 7B Instruct v0.3", ApiProvider.HUGGING_FACE),
        CaveModel("Qwen/Qwen2.5-7B-Instruct", "Qwen 2.5 7B Instruct", ApiProvider.HUGGING_FACE),
        CaveModel("microsoft/Phi-3-mini-4k-instruct", "Phi-3 Mini 4K Instruct", ApiProvider.HUGGING_FACE),
        CaveModel("google/gemma-2-2b-it", "Gemma 2 2B IT", ApiProvider.HUGGING_FACE),
        CaveModel("deepseek-ai/DeepSeek-R1-Distill-Qwen-1.5B", "DeepSeek R1 Distill 1.5B", ApiProvider.HUGGING_FACE)
    )

    suspend operator fun invoke(provider: ApiProvider): Result<List<CaveModel>> {
        return try {
            when (provider) {
                ApiProvider.GROQ -> Result.success(groqModels)
                ApiProvider.HUGGING_FACE -> Result.success(huggingFaceModels)
                ApiProvider.OPENROUTER -> {
                    val apiKey = settings.openRouterApiKey.first()
                    if (apiKey.isBlank()) {
                        return Result.success(openRouterModels)
                    }
                    try {
                        val api = NetworkModule.buildOpenRouterApi(apiKey.trim(), moshi)
                        val response = api.getModels()
                        val fetched = response.data
                            .filter { model ->
                                listOf("gpt", "claude", "gemini", "llama", "mistral",
                                    "deepseek", "qwen", "mixtral", "gemma", "command")
                                    .any { model.id.contains(it, ignoreCase = true) }
                            }
                            .take(30)
                            .map { CaveModel(it.id, it.name, ApiProvider.OPENROUTER) }
                        if (fetched.isEmpty()) Result.success(openRouterModels)
                        else Result.success(fetched)
                    } catch (e: Exception) {
                        Result.success(openRouterModels)
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

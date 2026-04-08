package com.omersusin.cavepressor.domain.usecase

import com.omersusin.cavepressor.data.datastore.SettingsDataStore
import com.omersusin.cavepressor.di.NetworkModule
import com.omersusin.cavepressor.domain.model.ApiProvider
import com.omersusin.cavepressor.domain.model.CaveModel
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FetchModelsUseCase @Inject constructor(
    private val settings: SettingsDataStore,
    private val moshi: Moshi
) {
    // Groq sabit model listesi — API bazen kısıtlı döner
    private val groqDefaultModels = listOf(
        CaveModel("llama-3.3-70b-versatile", "Llama 3.3 70B", ApiProvider.GROQ),
        CaveModel("llama-3.1-8b-instant", "Llama 3.1 8B Instant", ApiProvider.GROQ),
        CaveModel("llama3-70b-8192", "Llama 3 70B", ApiProvider.GROQ),
        CaveModel("llama3-8b-8192", "Llama 3 8B", ApiProvider.GROQ),
        CaveModel("mixtral-8x7b-32768", "Mixtral 8x7B", ApiProvider.GROQ),
        CaveModel("gemma2-9b-it", "Gemma 2 9B", ApiProvider.GROQ)
    )

    private val openRouterDefaultModels = listOf(
        CaveModel("openai/gpt-4o-mini", "GPT-4o Mini", ApiProvider.OPENROUTER),
        CaveModel("openai/gpt-4o", "GPT-4o", ApiProvider.OPENROUTER),
        CaveModel("anthropic/claude-3.5-haiku", "Claude 3.5 Haiku", ApiProvider.OPENROUTER),
        CaveModel("anthropic/claude-3.5-sonnet", "Claude 3.5 Sonnet", ApiProvider.OPENROUTER),
        CaveModel("meta-llama/llama-3.3-70b-instruct", "Llama 3.3 70B", ApiProvider.OPENROUTER),
        CaveModel("google/gemini-flash-1.5", "Gemini Flash 1.5", ApiProvider.OPENROUTER),
        CaveModel("mistralai/mixtral-8x7b-instruct", "Mixtral 8x7B", ApiProvider.OPENROUTER)
    )

    suspend operator fun invoke(provider: ApiProvider): Result<List<CaveModel>> {
        return try {
            val apiKey = when (provider) {
                ApiProvider.OPENROUTER -> settings.openRouterApiKey.first()
                ApiProvider.GROQ -> settings.groqApiKey.first()
            }

            if (apiKey.isBlank()) {
                val defaults = if (provider == ApiProvider.GROQ) groqDefaultModels
                else openRouterDefaultModels
                return Result.success(defaults)
            }

            when (provider) {
                ApiProvider.GROQ -> Result.success(groqDefaultModels)
                ApiProvider.OPENROUTER -> {
                    try {
                        val api = NetworkModule.buildOpenRouterApi(apiKey, moshi)
                        val response = api.getModels()
                        val models = response.data
                            .filter { it.id.contains("free").not() }
                            .take(20)
                            .map { CaveModel(it.id, it.name, ApiProvider.OPENROUTER) }
                        if (models.isEmpty()) Result.success(openRouterDefaultModels)
                        else Result.success(models)
                    } catch (e: Exception) {
                        Result.success(openRouterDefaultModels)
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

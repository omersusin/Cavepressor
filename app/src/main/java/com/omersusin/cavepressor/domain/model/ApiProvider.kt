package com.cavepressor.domain.model

enum class ApiProvider(val displayName: String, val baseUrl: String) {
    OPENROUTER("OpenRouter", "https://openrouter.ai/api/v1/"),
    GROQ("Groq", "https://api.groq.com/openai/v1/"),
    HUGGING_FACE("Hugging Face", "https://router.huggingface.co/v1/")
}

enum class CompressionLevel(val displayName: String, val targetReduction: String) {
    LIGHT("Light", "15-30%"),
    MEDIUM("Medium", "30-45%"),
    AGGRESSIVE("Aggressive", "45-60%")
}

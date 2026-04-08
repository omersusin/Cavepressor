package com.omersusin.cavepressor.domain.model

data class CompressionResult(
    val id: Long = 0,
    val originalText: String,
    val compressedText: String,
    val originalTokens: Int,
    val compressedTokens: Int,
    val reductionPercent: Int,
    val provider: ApiProvider,
    val model: String,
    val level: CompressionLevel,
    val timestamp: Long = System.currentTimeMillis()
)

data class CaveModel(
    val id: String,
    val displayName: String,
    val provider: ApiProvider
)

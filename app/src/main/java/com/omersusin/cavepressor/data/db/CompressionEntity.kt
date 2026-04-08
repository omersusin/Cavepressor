package com.omersusin.cavepressor.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compressions")
data class CompressionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalText: String,
    val compressedText: String,
    val originalTokens: Int,
    val compressedTokens: Int,
    val reductionPercent: Int,
    val provider: String,
    val model: String,
    val level: String,
    val timestamp: Long = System.currentTimeMillis()
)

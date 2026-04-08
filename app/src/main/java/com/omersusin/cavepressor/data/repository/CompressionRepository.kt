package com.omersusin.cavepressor.data.repository

import com.omersusin.cavepressor.data.db.CompressionDao
import com.omersusin.cavepressor.data.db.CompressionEntity
import com.omersusin.cavepressor.domain.model.ApiProvider
import com.omersusin.cavepressor.domain.model.CompressionLevel
import com.omersusin.cavepressor.domain.model.CompressionResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompressionRepository @Inject constructor(
    private val dao: CompressionDao
) {
    fun getHistory(): Flow<List<CompressionResult>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    suspend fun save(result: CompressionResult): Long =
        dao.insert(result.toEntity())

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun clearAll() = dao.deleteAll()

    fun getCount(): Flow<Int> = dao.getCount()

    private fun CompressionEntity.toDomain() = CompressionResult(
        id = id,
        originalText = originalText,
        compressedText = compressedText,
        originalTokens = originalTokens,
        compressedTokens = compressedTokens,
        reductionPercent = reductionPercent,
        provider = ApiProvider.valueOf(provider),
        model = model,
        level = CompressionLevel.valueOf(level),
        timestamp = timestamp
    )

    private fun CompressionResult.toEntity() = CompressionEntity(
        id = id,
        originalText = originalText,
        compressedText = compressedText,
        originalTokens = originalTokens,
        compressedTokens = compressedTokens,
        reductionPercent = reductionPercent,
        provider = provider.name,
        model = model,
        level = level.name,
        timestamp = timestamp
    )
}

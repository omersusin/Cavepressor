package com.omersusin.cavepressor.domain.usecase

import com.omersusin.cavepressor.data.repository.CompressionRepository
import com.omersusin.cavepressor.domain.model.CompressionResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val repository: CompressionRepository
) {
    operator fun invoke(): Flow<List<CompressionResult>> = repository.getHistory()

    suspend fun deleteById(id: Long) = repository.deleteById(id)

    suspend fun clearAll() = repository.clearAll()
}

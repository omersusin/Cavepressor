package com.omersusin.cavepressor.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CompressionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CompressionEntity): Long

    @Query("SELECT * FROM compressions ORDER BY timestamp DESC LIMIT 50")
    fun getAll(): Flow<List<CompressionEntity>>

    @Query("SELECT * FROM compressions WHERE id = :id")
    suspend fun getById(id: Long): CompressionEntity?

    @Query("DELETE FROM compressions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM compressions")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM compressions")
    fun getCount(): Flow<Int>
}

package com.cavepressor.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CompressionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun compressionDao(): CompressionDao
}

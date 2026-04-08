package com.omersusin.cavepressor.di

import android.content.Context
import androidx.room.Room
import com.omersusin.cavepressor.data.db.AppDatabase
import com.omersusin.cavepressor.data.db.CompressionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cavepressor.db"
        ).build()

    @Provides
    @Singleton
    fun provideCompressionDao(db: AppDatabase): CompressionDao =
        db.compressionDao()
}

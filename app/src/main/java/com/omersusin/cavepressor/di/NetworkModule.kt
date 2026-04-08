package com.omersusin.cavepressor.di

import com.omersusin.cavepressor.domain.model.ApiProvider
import com.omersusin.cavepressor.network.api.GroqApi
import com.omersusin.cavepressor.network.api.OpenRouterApi
import com.omersusin.cavepressor.network.interceptor.AuthInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private fun buildOkHttpClient(apiKey: String): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(apiKey))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun buildOpenRouterApi(apiKey: String, moshi: Moshi): OpenRouterApi =
        Retrofit.Builder()
            .baseUrl(ApiProvider.OPENROUTER.baseUrl)
            .client(buildOkHttpClient(apiKey))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenRouterApi::class.java)

    fun buildGroqApi(apiKey: String, moshi: Moshi): GroqApi =
        Retrofit.Builder()
            .baseUrl(ApiProvider.GROQ.baseUrl)
            .client(buildOkHttpClient(apiKey))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GroqApi::class.java)
}

package com.cavepressor.network.api

import com.cavepressor.network.model.ChatRequest
import com.cavepressor.network.model.ChatResponse
import com.cavepressor.network.model.OpenRouterModelsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OpenRouterApi {
    @POST("chat/completions")
    suspend fun compress(@Body request: ChatRequest): ChatResponse

    @GET("models")
    suspend fun getModels(): OpenRouterModelsResponse
}

package com.cavepressor.network.api

import com.cavepressor.network.model.ChatRequest
import com.cavepressor.network.model.ChatResponse
import com.cavepressor.network.model.HuggingFaceModelsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HuggingFaceApi {
    @POST("chat/completions")
    suspend fun compress(@Body request: ChatRequest): ChatResponse

    @GET("models")
    suspend fun getModels(): HuggingFaceModelsResponse
}

package com.cavepressor.network.api

import com.cavepressor.network.model.ChatRequest
import com.cavepressor.network.model.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface HuggingFaceApi {
    @POST("chat/completions")
    suspend fun compress(@Body request: ChatRequest): ChatResponse
}

package com.omersusin.cavepressor.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroqModelsResponse(
    @Json(name = "data") val data: List<GroqModel>
)

@JsonClass(generateAdapter = true)
data class GroqModel(
    @Json(name = "id") val id: String,
    @Json(name = "owned_by") val ownedBy: String
)

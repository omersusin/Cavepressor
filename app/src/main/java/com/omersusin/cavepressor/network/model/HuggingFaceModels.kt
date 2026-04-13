package com.cavepressor.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HuggingFaceModelsResponse(
    @Json(name = "data") val data: List<HuggingFaceModelData> = emptyList()
)

@JsonClass(generateAdapter = true)
data class HuggingFaceModelData(
    @Json(name = "id") val id: String = "",
    @Json(name = "object") val objectType: String = "",
    @Json(name = "created") val created: Long = 0
)

package com.example.newnews.domain.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    @SerialName("articles")
    val articles: MutableList<Article>? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("totalResults")
    val totalResults: Int? = null
)
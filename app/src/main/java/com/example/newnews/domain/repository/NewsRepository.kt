package com.example.newnews.domain.repository

import com.example.newnews.domain.model.NewsResponse
import retrofit2.Response

interface NewsRepository {

    suspend fun getNews(countryCode: String, pageNumber: Int): Response<NewsResponse>

}
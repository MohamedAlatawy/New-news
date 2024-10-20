package com.example.newnews.data.repo_impl

import com.example.newnews.data.api.RetrofitInstance
import com.example.newnews.domain.model.NewsResponse
import com.example.newnews.domain.repository.NewsRepository
import retrofit2.Response

class NewsRepositoryImpl: NewsRepository {

    override suspend fun getNews(countryCode: String, pageNumber: Int): Response<NewsResponse> {
        return RetrofitInstance.api.getNews(countryCode = countryCode, pageNumber = pageNumber)
    }
}
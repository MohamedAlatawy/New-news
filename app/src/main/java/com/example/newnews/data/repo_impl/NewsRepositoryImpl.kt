package com.example.newnews.data.repo_impl

import com.example.newnews.data.api.NewsApi
import com.example.newnews.data.api.RetrofitInstance
import com.example.newnews.domain.model.NewsResponse
import com.example.newnews.domain.repository.NewsRepository
import retrofit2.Response
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsApi
): NewsRepository {

    override suspend fun getNews(countryCode: String, pageNumber: Int): Response<NewsResponse> {
        return newsApi.getNews(countryCode = countryCode, pageNumber = pageNumber)
    }
}
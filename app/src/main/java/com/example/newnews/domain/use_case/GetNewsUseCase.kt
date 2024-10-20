package com.example.newnews.domain.use_case

import com.example.newnews.domain.model.NewsResponse
import com.example.newnews.domain.repository.NewsRepository
import retrofit2.Response
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
) {
    suspend operator fun invoke(
        countryCode: String = "us",
        pageNumber: Int
    ) : Response<NewsResponse> {
        return newsRepository.getNews(countryCode = countryCode, pageNumber = pageNumber)
    }
}
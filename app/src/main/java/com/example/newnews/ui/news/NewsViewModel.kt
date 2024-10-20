package com.example.newnews.ui.news

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newnews.domain.model.NewsResponse
import com.example.newnews.domain.use_case.GetNewsUseCase
import com.example.newnews.domain.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
    private val application: Application
) : ViewModel() {

    val news: MutableLiveData<DataState<NewsResponse>> = MutableLiveData()
    var pageNumber = 1
    var newsResponse: NewsResponse? = null

    init {
        getNews(countryCode = "us")
    }

    fun getNews(countryCode: String) = viewModelScope.launch {
        newsInternet(countryCode)
    }

    private fun handleNewsResponse(response: Response<NewsResponse>): DataState<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                pageNumber++
                if (newsResponse == null) {
                    newsResponse = resultResponse
                }else{
                    val oldNews = newsResponse?.articles
                    val newNews = resultResponse.articles
                    newNews?.let { oldNews?.addAll(it) }
                }
                return DataState.Success(newsResponse ?: resultResponse)
            }
        }
        return DataState.Error(response.message())
    }

    fun internetConnection(context: Context): Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            } ?: false
        }
    }

    private suspend fun newsInternet(countryCode: String) {
        news.postValue(DataState.Loading())
        try {
            if (internetConnection(application)) {
                val response = getNewsUseCase.invoke(countryCode, pageNumber)
                news.postValue(handleNewsResponse(response))
            }else{
                news.postValue(DataState.Error("No Internet Connection"))
            }
        } catch (t: Throwable) {
            when(t){
                is IOException -> news.postValue(DataState.Error("Network Failure"))
                else -> news.postValue(DataState.Error("Conversion Error"))
            }
        }
    }

}
package com.example.newnews.data.di

import com.example.newnews.data.api.NewsApi
import com.example.newnews.data.repo_impl.NewsRepositoryImpl
import com.example.newnews.domain.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideNewsRepository(
        newsApi: NewsApi
    ): NewsRepository {
        return NewsRepositoryImpl(newsApi = newsApi)
    }
}
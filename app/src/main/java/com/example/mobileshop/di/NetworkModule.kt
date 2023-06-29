package com.example.mobileshop.di

import com.example.mobileshop.network.ApiService
import com.example.mobileshop.network.ApiServiceImpl
import com.example.mobileshop.network.TokenInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    fun providesUrl()= "https://dummyjson.com/"
    @Provides
    @Singleton
    fun providesTokenInterceptor(): TokenInterceptor {
        val token = "random token"
        return TokenInterceptor(token)
    }

    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().setLevel(
        HttpLoggingInterceptor.Level.BASIC)

    @Provides
    fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder().addInterceptor(providesLoggingInterceptor()).build()

    @Provides
    @Singleton
    fun providesApiService(url: String, tokenInterceptor: TokenInterceptor) : ApiService =
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(providesOkHttpClient())
            .build()
            .create(ApiService::class.java)//http log interceptor and token interceptor

    @Provides
    fun provideApiService(): ApiServiceImpl =
        ApiServiceImpl(providesApiService(providesUrl(), providesTokenInterceptor()) )
}
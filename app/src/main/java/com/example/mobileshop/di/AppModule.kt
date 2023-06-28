package com.example.mobileshop.di

import android.content.Context
import androidx.room.Room
import com.example.mobileshop.api_recycler_view.ApiService
import com.example.mobileshop.api_recycler_view.ApiServiceImpl
import com.example.mobileshop.api_recycler_view.TokenInterceptor
import com.example.mobileshop.db.AppDatabase
import com.example.mobileshop.db.LocalImageDao
import com.example.mobileshop.db.ProductDao
import com.example.mobileshop.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun providesMainRepository(productDao: ProductDao, localImageDao: LocalImageDao): MainRepository =    MainRepository(
        NetworkModule.provideApiService(), productDao, localImageDao
    )
}
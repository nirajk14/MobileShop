package com.example.mobileshop.di

import android.content.Context
import androidx.room.Room
import com.example.mobileshop.api_recycler_view.ApiService
import com.example.mobileshop.api_recycler_view.ApiServiceImpl
import com.example.mobileshop.db.AppDatabase
import com.example.mobileshop.db.ProductDao
import com.example.mobileshop.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesUrl()= "https://dummyjson.com/"

    @Provides
    @Singleton
    fun providesApiService(url: String) : ApiService =
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context) : AppDatabase =
        Room.databaseBuilder(context,AppDatabase::class.java,"userDatabase")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providesProductDao(appDatabase: AppDatabase) : ProductDao =
        appDatabase.productDao()


    @Provides
    fun provideApiService(): ApiServiceImpl =
        ApiServiceImpl(providesApiService(providesUrl()))
    @Provides
    fun providesMainRepository(productDao: ProductDao): MainRepository =    MainRepository(
        provideApiService(), productDao
    )
}
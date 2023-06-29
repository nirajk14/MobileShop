package com.example.mobileshop.di

import com.example.mobileshop.db.LocalImageDao
import com.example.mobileshop.db.ProductDao
import com.example.mobileshop.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun providesMainRepository(
        productDao: ProductDao,
        localImageDao: LocalImageDao
    ): MainRepository = MainRepository(
        NetworkModule.provideApiService(), productDao, localImageDao
    )
}
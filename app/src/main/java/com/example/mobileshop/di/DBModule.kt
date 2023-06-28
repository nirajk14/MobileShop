package com.example.mobileshop.di

import android.content.Context
import androidx.room.Room
import com.example.mobileshop.db.AppDatabase
import com.example.mobileshop.db.LocalImageDao
import com.example.mobileshop.db.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context) : AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java,"userDatabase")
            .fallbackToDestructiveMigration()
            .build()



    @Provides
    fun providesProductDao(appDatabase: AppDatabase) : ProductDao =
        appDatabase.productDao()

    @Provides
    fun providesLocalImageDao(appDatabase: AppDatabase): LocalImageDao =
        appDatabase.localImageDao()
}
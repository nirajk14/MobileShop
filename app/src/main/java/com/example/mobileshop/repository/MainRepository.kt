package com.example.mobileshop.repository

import com.example.mobileshop.ApiRecyclerView.ApiResponse
import com.example.mobileshop.ApiRecyclerView.ApiServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiServiceImpl: ApiServiceImpl) {

    fun getProducts(): Flow<ApiResponse> = flow {
        emit(apiServiceImpl.getProducts())
    }.flowOn(Dispatchers.IO)
}
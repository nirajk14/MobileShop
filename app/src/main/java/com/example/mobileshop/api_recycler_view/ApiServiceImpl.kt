package com.example.mobileshop.api_recycler_view


import javax.inject.Inject

class ApiServiceImpl @Inject constructor(private val apiService: ApiService) {

    suspend fun getProducts(): ApiResponse = apiService.getProducts()

    suspend fun getProducts(limit: Int, skip: Int): ApiResponse = apiService.getProducts(limit,skip)

    suspend fun getProductById(productId: Int): ApiResponse = apiService.getProductById(productId)
}
package com.example.mobileshop.network


import com.example.mobileshop.model.ApiResponse
import com.example.mobileshop.model.Product
import javax.inject.Inject

class ApiServiceImpl @Inject constructor(private val apiService: ApiService) {

    suspend fun getProducts(): ApiResponse = apiService.getProducts()

    suspend fun getProducts(limit: Int, skip: Int): ApiResponse = apiService.getProducts(limit,skip)

    suspend fun getProductById(productId: Int): Product = apiService.getProductById(productId)
}
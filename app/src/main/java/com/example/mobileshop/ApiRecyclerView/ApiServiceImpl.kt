package com.example.mobileshop.ApiRecyclerView


import javax.inject.Inject

class ApiServiceImpl @Inject constructor(private val apiService: ApiService) {

    suspend fun getProducts(): ApiResponse = apiService.getProducts()
}
package com.example.mobileshop.api_recycler_view


import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {


    @Headers("Content-Type:application/json")
    @GET("/products")
    suspend fun getProducts(): ApiResponse


}
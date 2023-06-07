package com.example.mobileshop.ApiRecyclerView


import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {


    @Headers("Content-Type:application/json")
    @GET("/products")
    suspend fun getProducts(): ApiResponse


}
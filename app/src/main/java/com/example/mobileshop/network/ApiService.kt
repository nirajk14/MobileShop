package com.example.mobileshop.network


import com.example.mobileshop.model.ApiResponse
import com.example.mobileshop.model.Product
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Headers("Content-Type:application/json")
    @GET("/products")
    suspend fun getProducts(): ApiResponse


    @Headers("Content-Type:application/json")
    @GET("/products")
    suspend fun getProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ApiResponse
    @Headers("Content-Type:application/json")
    @GET("/products/{productId}")
    suspend fun getProductById(
        @Path("productId") productId: Int
    ): Product


}
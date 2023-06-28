package com.example.mobileshop.api_recycler_view

import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Retrieve the original request from the chain
        val originalRequest = chain.request()

        // Add the token to the request headers
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        // Proceed with the authenticated request
        val response = chain.proceed(authenticatedRequest)

        // You can perform additional operations on the response if needed

        return response
    }
}
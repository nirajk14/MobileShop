package com.example.mobileshop.repository

import com.example.mobileshop.api_recycler_view.ApiResponse
import com.example.mobileshop.api_recycler_view.ApiServiceImpl
import com.example.mobileshop.api_recycler_view.Products
import com.example.mobileshop.db.ProductDao
import com.example.mobileshop.db.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiServiceImpl: ApiServiceImpl,
private val productDao: ProductDao) {

    fun getProducts(): Flow<ApiResponse> = flow {
        emit(apiServiceImpl.getProducts())
    }.flowOn(Dispatchers.IO)


    suspend fun insertProduct(products: Products) {
        val productEntity = ProductEntity(
            id = products.id!!,
            title = products.title,
            description = products.description,
            price = products.price,
            discountPercentage = products.discountPercentage,
            rating = products.rating,
            stock = products.stock,
            brand = products.brand,
            category = products.category,
            thumbnail = products.thumbnail,
            images = products.images
        )
        productDao.insert(productEntity)
    }

    suspend fun insertApiDataToDB(apiResponse: ApiResponse) {
        apiResponse.products.map { product ->
            insertProduct(product)
        }
    }

    suspend fun getAllProducts(refresh: Boolean): Flow<List<ProductEntity>> = flow{
        if (refresh)
            insertApiDataToDB(apiServiceImpl.getProducts())
        emit(productDao.getAllProducts())
    }.flowOn(Dispatchers.IO)

//    {
//        return productDao.getAllProducts()
//    }
}
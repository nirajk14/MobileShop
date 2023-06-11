package com.example.mobileshop.repository

import com.example.mobileshop.ApiRecyclerView.ApiResponse
import com.example.mobileshop.ApiRecyclerView.ApiServiceImpl
import com.example.mobileshop.ApiRecyclerView.Products
import com.example.mobileshop.db.AppDatabase
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
        for (product in apiResponse.products) {
            insertProduct(product)
        }
    }

    suspend fun getAllProducts(): Flow<List<ProductEntity>> = flow{
        productDao.deleteAllProducts()
        insertApiDataToDB(apiServiceImpl.getProducts())
        emit(productDao.getAllProducts())
    }.flowOn(Dispatchers.IO)

//    {
//        return productDao.getAllProducts()
//    }
}
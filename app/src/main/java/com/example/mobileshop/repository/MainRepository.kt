package com.example.mobileshop.repository

import com.example.mobileshop.api_recycler_view.ApiResponse
import com.example.mobileshop.api_recycler_view.ApiServiceImpl
import com.example.mobileshop.api_recycler_view.Product
import com.example.mobileshop.db.LocalImageDao
import com.example.mobileshop.db.LocalImageEntity
import com.example.mobileshop.db.ProductDao
import com.example.mobileshop.db.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiServiceImpl: ApiServiceImpl,
private val productDao: ProductDao,
private val localImageDao: LocalImageDao) {

    fun getProducts(): Flow<ApiResponse> = flow {
        emit(apiServiceImpl.getProducts())
    }.flowOn(Dispatchers.IO)


    suspend fun insertProduct(productEntity: ProductEntity) {
        productDao.insert(productEntity)
    }

    suspend fun insertProduct(product: Product, localImageBoolean: Boolean) {
        val productEntity = ProductEntity(
            id = product.id!!,
            title = product.title,
            description = product.description,
            price = product.price,
            discountPercentage = product.discountPercentage,
            rating = product.rating,
            stock = product.stock,
            brand = product.brand,
            category = product.category,
            thumbnail = product.thumbnail,
            images = product.images,
            localImages = localImageBoolean,
        )
        productDao.insert(productEntity)
    }

    suspend fun insertApiDataToDB(apiResponse: ApiResponse) {
        apiResponse.products.map { product ->
            insertProduct(product, false)
        }
    }

    suspend fun getAllProducts(refresh: Boolean): Flow<List<ProductEntity>> = flow{
        if (refresh)
            insertApiDataToDB(apiServiceImpl.getProducts())
        emit(productDao.getAllProducts())
    }.flowOn(Dispatchers.IO)

    suspend fun insertLocalImage(localImageEntity: LocalImageEntity, productEntity: ProductEntity) {
        val productEntityCopy= productEntity.copy(localImages = true)
        insertProduct(productEntityCopy)
        localImageDao.insert(localImageEntity)
    }

    fun getCorrectLocalImages(productId: Int): Flow<List<LocalImageEntity>> = flow{
        emit(localImageDao.findById(productId))
    }.flowOn(Dispatchers.IO)

    fun getAllLocalImages(): Flow<List<LocalImageEntity>> = flow {
        emit(localImageDao.getAllLocalImages())
    }.flowOn(Dispatchers.IO)

    fun getSingleLocalImage(productId: Int): Flow<LocalImageEntity> = flow {
        emit(localImageDao.getSingleImage(productId)[0])
    }.flowOn(Dispatchers.IO)

}
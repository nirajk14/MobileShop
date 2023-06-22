package com.example.mobileshop.repository

import com.example.mobileshop.api_recycler_view.ApiResponse
import com.example.mobileshop.api_recycler_view.ApiServiceImpl
import com.example.mobileshop.api_recycler_view.Product
import com.example.mobileshop.db.LocalImageDao
import com.example.mobileshop.db.LocalImageEntity
import com.example.mobileshop.db.ProductDao
import com.example.mobileshop.db.ProductWithLocalImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val apiServiceImpl: ApiServiceImpl,
    private val productDao: ProductDao,
    private val localImageDao: LocalImageDao
) {


    private suspend fun insertProduct(product: Product) {
        productDao.insert(product)
    }

    private suspend fun insertApiDataToDB(apiResponse: ApiResponse) {
        apiResponse.products.map { product ->
            insertProduct(product)
        }
    }

    suspend fun getAllProducts(refresh: Boolean): Flow<List<Product>> = flow {
        if (refresh)
            insertApiDataToDB(apiServiceImpl.getProducts())
        emit(productDao.getAllProducts())
    }.flowOn(Dispatchers.IO)

    suspend fun insertLocalImage(localImageEntity: LocalImageEntity) {
        localImageDao.insert(localImageEntity)
    }

    suspend fun insertImageToRecyclerView(url: String, productId: Int) {
        var id = 0
        if (localImageDao.getMaxIdHavingProductId(productId) != null) {
            id = localImageDao.getMaxIdHavingProductId(productId) + 1
        } else {
            id = 0
        }
        val localImageEntity = LocalImageEntity(
            id = id,
            imageUrl = url,
            productId = productId
        )
        localImageDao.insert(localImageEntity)

    }

    fun getLocalImagesForProduct(productId: Int): Flow<List<LocalImageEntity>> =
        localImageDao.getLocalImagesForProduct(productId).flowOn(Dispatchers.IO)
}





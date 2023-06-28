package com.example.mobileshop.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.mobileshop.api_recycler_view.ApiResponse
import com.example.mobileshop.api_recycler_view.ApiServiceImpl
import com.example.mobileshop.api_recycler_view.Product
import com.example.mobileshop.db.LocalImageDao
import com.example.mobileshop.db.LocalImageEntity
import com.example.mobileshop.db.ProductDao
import com.example.mobileshop.db.ProductWithLocalImages
import com.example.mobileshop.paging.ProductPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainRepository @Inject constructor(
    val apiServiceImpl: ApiServiceImpl,
    val productDao: ProductDao,
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

    suspend fun getAllProducts(limit: Int, skip: Int, insertDB: Boolean): Flow<List<Product>> = flow {
        if (insertDB)
            insertApiDataToDB(apiServiceImpl.getProducts(limit,skip))
        emit(productDao.getAllProducts())
    }.flowOn(Dispatchers.IO)

    suspend fun getAllProductsfromDB(pageNumber: Int, pageSize: Int, refresh: Boolean): Flow<List<Product>> = flow {
        val offset = (pageNumber - 1) * pageSize
        var products: List<Product>

        if (refresh)
            insertApiDataToDB(apiServiceImpl.getProducts())
        products = productDao.getProductsByPage(pageSize, offset)
        emit(products)
    }.flowOn(Dispatchers.IO)

    fun getProducts()= Pager(
        config = PagingConfig(0, maxSize = 30),
        pagingSourceFactory ={
            ProductPagingSource(productDao,apiServiceImpl)
        }
    ).flow

    suspend fun insertLocalImage(localImageEntity: LocalImageEntity) {
        localImageDao.insert(localImageEntity)
    }

    suspend fun insertImageToRecyclerView(url: String, productId: Int) {

        val id = if (localImageDao.getMaxIdHavingProductId(productId) != null) {
            localImageDao.getMaxIdHavingProductId(productId) + 1
        } else {
            0
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





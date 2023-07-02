package com.example.mobileshop.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.mobileshop.model.ApiResponse
import com.example.mobileshop.network.ApiServiceImpl
import com.example.mobileshop.model.Product
import com.example.mobileshop.db.LocalImageDao
import com.example.mobileshop.model.LocalImageEntity
import com.example.mobileshop.db.ProductDao
import com.example.mobileshop.main_view.ProductPagingSource
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

    suspend fun completeList() = apiServiceImpl.getProducts(100,0).products

    suspend fun categoryList():Flow<List<String>> = flow{ emit(completeList().mapNotNull { it.category }.distinct()) }.flowOn(Dispatchers.IO)

    suspend fun getProductById(productId: Int): Flow<Product> = flow {
        emit(apiServiceImpl.getProductById(productId))
    }.flowOn(Dispatchers.IO)


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

    fun getProducts(insertDB: Boolean, searchQuery: String?,chipQuery: List<String>)= Pager(
        config = PagingConfig(pageSize = 6), //the pageSize is equal to params.loadSize
        pagingSourceFactory ={
            ProductPagingSource(productDao,apiServiceImpl,insertDB, searchQuery, chipQuery)
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





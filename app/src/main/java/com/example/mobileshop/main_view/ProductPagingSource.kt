package com.example.mobileshop.main_view

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mobileshop.network.ApiServiceImpl
import com.example.mobileshop.model.Product
import com.example.mobileshop.db.ProductDao
import timber.log.Timber
import java.lang.Exception

class ProductPagingSource(
    private val productDao: ProductDao,
    private val apiServiceImpl: ApiServiceImpl,
    private val insertDB: Boolean,
    private val searchQuery: String?,
    private val chipQuery: List<String>
) : PagingSource<Int, Product>() {

    private suspend fun insertProduct(products: List<Product>) {
        products.forEach { product ->
            productDao.insert(product)
        }
    }


    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { position -> state.closestItemToPosition(position)?.id }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val page = params.key ?: 1
            val limit = params.loadSize
            val skip = (page - 1) * limit
            var prevKey: Int? = null
            var nextKey: Int? = null

            val response = apiServiceImpl.getProducts(limit, skip)
//            Timber.i(response.products.size)
            if (insertDB)
                insertProduct(response.products)

            val filteredList = filterList(limit, skip,response.products)

            if (searchQuery != null || chipQuery.isNotEmpty()) {
                prevKey = null
                nextKey = null
            } else {
                prevKey = if (page > 1) page - 1 else null
                nextKey = if (response.products.isNotEmpty()) page + 1 else null
            }

            LoadResult.Page(
                data = filteredList,
                prevKey,
                nextKey
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

    private suspend fun filterList(limit: Int, skip: Int, defaultResponse: List<Product>): List<Product> {
        if (chipQuery.isNotEmpty() || searchQuery != null) {
            val completeList = apiServiceImpl.getProducts(100, 0).products

            val chipFilteredList = if (chipQuery.isNotEmpty()) {
                completeList.filter { product ->
                    val productCategory = product.category
                    chipQuery.any { chipText ->
                        productCategory?.contains(chipText, ignoreCase = true) == true
                    }
                }
            } else {
                completeList
            }

            val searchFilteredList = if (searchQuery != null) {
                chipFilteredList.filter { product ->
                    product.title?.contains(searchQuery, ignoreCase = true) == true
                }
            } else {
                chipFilteredList
            }

            return searchFilteredList
        }
        return defaultResponse
    }
}

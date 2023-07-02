package com.example.mobileshop.main_view

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mobileshop.network.ApiServiceImpl
import com.example.mobileshop.model.Product
import com.example.mobileshop.db.ProductDao
import java.lang.Exception

class ProductPagingSource(private val productDao: ProductDao, private val apiServiceImpl: ApiServiceImpl, private val insertDB: Boolean, private val searchQuery: String?): PagingSource<Int, Product>() {

    private suspend fun insertProduct(products: List<Product>) {
        products.forEach { product ->
            productDao.insert(product)
        }
    }



    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { position-> state.closestItemToPosition(position)?.id }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val page = params.key ?: 1
            val limit= params.loadSize
            val skip= (page-1) * limit
            var prevKey: Int? = null
            var nextKey: Int? = null

            val response = apiServiceImpl.getProducts(limit, skip)
            println(response.products.size)
            if (insertDB)
                insertProduct(response.products)

            val filteredList = if (searchQuery != null) {
                apiServiceImpl.getProducts(100,0).products.filter { product ->
                    product.title!!.contains(searchQuery, ignoreCase = true)
                }
            } else {
                response.products
            };

            if (searchQuery!=null){
                prevKey = null
                nextKey = null
            }
            else{
                prevKey = if(page>1) page- 1  else null
                nextKey = if (response.products.isNotEmpty()) page + 1 else null
            }

            LoadResult.Page(
                data = filteredList,
                prevKey,
                nextKey
            )
        } catch (e: Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
    }

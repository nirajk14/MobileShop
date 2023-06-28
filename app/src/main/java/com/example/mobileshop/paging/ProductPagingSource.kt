package com.example.mobileshop.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mobileshop.api_recycler_view.ApiServiceImpl
import com.example.mobileshop.api_recycler_view.Product
import com.example.mobileshop.db.ProductDao
import java.lang.Exception

class ProductPagingSource(private val productDao: ProductDao, private val apiServiceImpl: ApiServiceImpl): PagingSource<Int, Product>() {



    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { position-> state.closestItemToPosition(position)?.id }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val page = params.key ?: 1
            val limit= params.loadSize
            val skip= (page-1) * limit

            val response = apiServiceImpl.getProducts(limit, skip)
            println(response.products.size)
            LoadResult.Page(
                data = response.products,
                prevKey = if (page>1) page-1 else null,
                nextKey = if (response.products.isNotEmpty()) page +1 else null
            )
        } catch (e: Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
    }

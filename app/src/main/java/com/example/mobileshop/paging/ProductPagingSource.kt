package com.example.mobileshop.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mobileshop.api_recycler_view.ApiServiceImpl
import com.example.mobileshop.api_recycler_view.Product
import com.example.mobileshop.db.ProductDao
import java.lang.Exception

class ProductPagingSource(private val productDao: ProductDao, private val apiServiceImpl: ApiServiceImpl): PagingSource<Int, Product>() {

    private val initialPage = 1
    private val pageSize = 10
    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { position-> state.closestItemToPosition(position)?.id }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val page = params.key ?: initialPage
            val offset = (page - 1) * pageSize

//            val position = params.key?:1
            val response = apiServiceImpl.getProducts()
            println(response.products.size)
            LoadResult.Page(
                data = response.products,
                prevKey = if(page==1)  null else page - 1,
                nextKey = if (response.products.isEmpty()) null else page + 1
            )
        } catch (e: Exception){
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
    }

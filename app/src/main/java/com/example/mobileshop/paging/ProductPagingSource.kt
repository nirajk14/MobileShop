package com.example.mobileshop.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mobileshop.api_recycler_view.Product
import com.example.mobileshop.db.ProductDao
import java.lang.Exception

class ProductPagingSource(private val productDao: ProductDao): PagingSource<Int, Product>() {
    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        try {
            val position = params.key?:1
            val response = productDao.getAllProducts()
            return LoadResult.Page(
                data = response,
                prevKey = if(position==0)  null else position - 1,
                nextKey = if (position==response.size -1) null else position + 1
            )
        } catch (e: Exception){
            e.printStackTrace()
            return LoadResult.Error(e)
        }
    }
    }

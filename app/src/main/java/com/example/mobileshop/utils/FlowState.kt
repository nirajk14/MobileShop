package com.example.mobileshop.utils

import androidx.paging.PagingData
import com.example.mobileshop.model.LocalImageEntity
import com.example.mobileshop.model.Product
import kotlinx.coroutines.flow.Flow


//A class to contain state flow and shared flow states
sealed class FlowState {
    object Loading: FlowState()
    class Failure(val msg: Throwable): FlowState()
    class SuccessProductPaging(val data: PagingData<Product>) : FlowState()

    class SuccessProduct(val data: Product): FlowState()

    class SuccessProductCategory(val category: List<String>): FlowState()

    class SuccessProductWithLocalImageFlow(val data: Flow<List<LocalImageEntity>>): FlowState()
    class SuccessProductWithLocalImage(val data: List<LocalImageEntity>): FlowState()
    object Empty: FlowState()
}
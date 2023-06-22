package com.example.mobileshop.db

import com.example.mobileshop.api_recycler_view.Product
import kotlinx.coroutines.flow.Flow

sealed class DBState {
    object Loading: DBState()
    class Failure(val msg: Throwable): DBState()
    class SuccessProduct(val data: List<Product>) :DBState()

    class SuccessProductWithLocalImageFlow(val data: Flow<List<LocalImageEntity>>): DBState()
    class SuccessProductWithLocalImage(val data: List<LocalImageEntity>): DBState()
    object Empty: DBState()
}
package com.example.mobileshop.db

sealed class DBState {
    object Loading: DBState()
    class Failure(val msg: Throwable): DBState()
    class SuccessProduct(val data: List<ProductEntity>) :DBState()

    class SuccessProductWithLocalImage(val data: ProductWithLocalImages?): DBState()
    object Empty: DBState()
}
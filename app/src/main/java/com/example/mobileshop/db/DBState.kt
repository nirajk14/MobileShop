package com.example.mobileshop.db

sealed class DBState {
    object Loading: DBState()
    class Failure(val msg: Throwable): DBState()
    class SuccessProduct(val data: List<ProductEntity>) :DBState()

    class SuccessLocalImage(val data: List<LocalImageEntity>): DBState()
    object Empty: DBState()
}
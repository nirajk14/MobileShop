package com.example.mobileshop.db

sealed class DBState {
    object Loading: DBState()
    class Failure(val msg: Throwable): DBState()
    class SuccessProduct(val data: List<ProductEntity>) :DBState()

    class SuccessLocalImage(val data: List<LocalImageEntity>): DBState()

    class SuccessSingleLocalImage(val data: LocalImageEntity): DBState()
    object Empty: DBState()
}
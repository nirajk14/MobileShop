package com.example.mobileshop.db

sealed class DBState {
    object Loading: DBState()
    class Failure(val msg: Throwable): DBState()
    class Success(val data: List<ProductEntity>) :DBState()
    object Empty: DBState()
}
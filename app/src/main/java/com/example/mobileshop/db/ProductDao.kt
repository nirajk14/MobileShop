package com.example.mobileshop.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDao {
    @Insert
    suspend fun insert(product: ProductEntity)

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}
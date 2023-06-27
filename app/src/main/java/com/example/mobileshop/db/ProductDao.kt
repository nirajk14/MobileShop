package com.example.mobileshop.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mobileshop.api_recycler_view.Product

@Dao
interface ProductDao {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg product: Product)

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Int): Product?

    @Query("SELECT * FROM products LIMIT :pageSize OFFSET :offset")
    suspend fun getProductsByPage(pageSize: Int, offset: Int): List<Product>


}
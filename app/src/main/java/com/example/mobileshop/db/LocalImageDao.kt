package com.example.mobileshop.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow


@Dao
interface LocalImageDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg localImageEntity: LocalImageEntity)


    @Query("SELECT MAX(id) FROM LOCAL_IMAGE WHERE productId = :productId")
    suspend fun getMaxIdHavingProductId(productId: Int): Int

    @Query("SELECT * FROM local_image WHERE productId = :productId ORDER BY productId ASC")
    fun getLocalImagesForProduct(productId: Int): Flow<List<LocalImageEntity>>

    @Query("SELECT * FROM local_image WHERE productId = :productId ORDER BY productId ASC")
    suspend fun getLocalImagesForProductNoFlow(productId: Int): List<LocalImageEntity>

}
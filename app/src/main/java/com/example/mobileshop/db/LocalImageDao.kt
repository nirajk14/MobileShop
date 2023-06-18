package com.example.mobileshop.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface LocalImageDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(localImageEntity: LocalImageEntity)

    @Query("SELECT * FROM local_image WHERE productId LIKE :productId")
    suspend fun findById(productId: Int): List<LocalImageEntity>

}
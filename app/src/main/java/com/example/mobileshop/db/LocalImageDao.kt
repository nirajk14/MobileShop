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

    @Query("SELECT * FROM local_image WHERE productId = :productId AND id = 1") //This only makes sense if productId and id makes up a composite key
    suspend fun getSingleImage(productId: Int): List<LocalImageEntity>

    @Query("SELECT * FROM local_image")
    suspend fun getAllLocalImages(): List<LocalImageEntity>

}
package com.example.mobileshop.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName="local_image")
data class LocalImageEntity (
    @PrimaryKey (autoGenerate = true) val id: Int?,
    val imageUrl: String?,
    val productId: Int?
    )
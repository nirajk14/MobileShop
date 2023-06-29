package com.example.mobileshop.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(tableName="local_image", primaryKeys = ["productId", "id"], foreignKeys = [
    ForeignKey(
        entity = Product::class,
        parentColumns = ["id"],
        childColumns = ["productId"],
        onDelete = ForeignKey.CASCADE
    )
])
data class LocalImageEntity (
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "imageUrl")
    val imageUrl: String?,
    @ColumnInfo(name = "productId")
    val productId: Int
    )
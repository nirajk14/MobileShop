package com.example.mobileshop.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.mobileshop.api_recycler_view.Product


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
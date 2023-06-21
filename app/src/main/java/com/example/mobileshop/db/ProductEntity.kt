package com.example.mobileshop.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    var id: Int = 0,
    val title: String? = null,
    val description: String? = null,
    val price: Int? = null,
    val discountPercentage: Double? = null,
    val rating: Double? = null,
    val stock: Int? = null,
    val brand: String? = null,
    val category: String? = null,
    val thumbnail: String? = null,
    val images: List<String> = emptyList(),
) : java.io.Serializable
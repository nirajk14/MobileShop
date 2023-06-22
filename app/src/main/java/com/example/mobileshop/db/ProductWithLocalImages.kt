package com.example.mobileshop.db

import androidx.room.Embedded
import androidx.room.Relation
import com.example.mobileshop.api_recycler_view.Product

data class ProductWithLocalImages (
    @Embedded val product: Product,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    )
    val localImageEntities: List<LocalImageEntity>
        )
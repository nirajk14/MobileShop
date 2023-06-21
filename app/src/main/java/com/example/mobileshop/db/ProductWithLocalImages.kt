package com.example.mobileshop.db

import androidx.room.Embedded
import androidx.room.Relation

data class ProductWithLocalImages (
    @Embedded val productEntity: ProductEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    )
    val localImageEntities: List<LocalImageEntity>
        )
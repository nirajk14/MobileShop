package com.example.mobileshop.api_recycler_view


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

final data class ApiResponse(

    @SerializedName("products") var products: List<Product> = arrayListOf(),
    @SerializedName("total") var total: Int? = null,
    @SerializedName("skip") var skip: Int? = null,
    @SerializedName("limit") var limit: Int? = null

): java.io.Serializable


@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    @SerializedName("id") var id: Int = 0,
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("price") var price: Int? = null,
    @SerializedName("discountPercentage") var discountPercentage: Double? = null,
    @SerializedName("rating") var rating: Double? = null,
    @SerializedName("stock") var stock: Int? = null,
    @SerializedName("brand") var brand: String? = null,
    @SerializedName("category") var category: String? = null,
    @SerializedName("thumbnail") var thumbnail: String? = null,
    @SerializedName("images") var images: List<String> = listOf()

): java.io.Serializable
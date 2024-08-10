package com.mustfaibra.roffu.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class CategoryResponse(
    @SerializedName("data") val data: CategoryData
)

data class CategoryData(
    @SerializedName("categories") val categories: List<Category>,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("currentPage") val currentPage: Int
)

@Serializable
data class Category(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

package com.mustfaibra.roffu.models

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("data") val data: CategoryData
)

data class CategoryData(
    @SerializedName("categories") val categories: List<Category>,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("currentPage") val currentPage: Int
)

data class Category(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

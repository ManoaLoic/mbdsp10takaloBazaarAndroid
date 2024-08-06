package com.mustfaibra.roffu.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class Object(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("created_at") val createdAt: Date,
    @SerializedName("updated_at") val updatedAt: Date,
    @SerializedName("status") val status: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("deleted_At") val deletedAt: Date?,
    @SerializedName("user") val user: CustomUser? = null,
    @SerializedName("category") val category: Category? = null
)

data class ObjectRequest(
    val name: String,
    val description: String,
    val category_id: Int,
    val image_file: String
)

data class ObjectResponse(
    val objects: List<Object>,
    val totalPages: Int,
    val currentPage: Int
)

data class ObjectListResponse(
    val data: ObjectResponse
)

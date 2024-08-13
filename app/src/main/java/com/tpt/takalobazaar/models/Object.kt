package com.tpt.takalobazaar.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Object(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("image") val image: String?,
    @Contextual @SerializedName("created_at") val createdAt: Date,
    @Contextual @SerializedName("updated_at") val updatedAt: Date,
    @SerializedName("status") val status: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("category_id") val categoryId: Int,
    @Contextual @SerializedName("deleted_At") val deletedAt: Date?,
    @SerializedName("user") val user: CustomUser? = null,
    @SerializedName("category") val category: Category? = null
)

@Serializable
data class ObjectRequest(
    val name: String,
    val description: String,
    val category_id: Int,
    val image_file: String
)

@Serializable
data class ObjectResponse(
    val objects: List<Object>,
    val totalPages: Int,
    val currentPage: Int
)

@Serializable
data class UpdateObject(
    val name: String,
    val description: String,
    val category_id: Int,
    val image_file: String?
)

@Serializable
data class UpdateObjectResponse(
    val message: String,
    val data: Object
)

data class ObjectListResponse(
    val data: ObjectResponse
)

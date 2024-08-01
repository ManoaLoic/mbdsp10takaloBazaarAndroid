package com.mustfaibra.roffu.models

import kotlinx.serialization.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mustfaibra.roffu.utils.getFormattedDate
import java.util.*

@Serializable
data class User(
    @Expose
    @SerializedName("id")
    val id: Int,

    @Expose
    @SerializedName("username")
    val username: String,

    @Expose
    @SerializedName("last_name")
    val lastName: String,

    @Expose
    @SerializedName("first_name")
    val firstName: String,

    @Expose
    @SerializedName("profile_picture")
    val profilePicture: String? = null,

    @Expose
    @SerializedName("gender")
    val gender: String,

    @Expose
    @SerializedName("type")
    val type: String,

    @Expose
    @SerializedName("created_at")
    val createdAt: String = Date().getFormattedDate("yyyy-MM-dd HH:mm"),

    @Expose
    @SerializedName("updated_at")
    val updatedAt: String = Date().getFormattedDate("yyyy-MM-dd HH:mm"),

    @Expose
    @SerializedName("deleted_at")
    val deletedAt: String = Date().getFormattedDate("yyyy-MM-dd HH:mm"),

    @Expose
    @SerializedName("status")
    val status: String,
)

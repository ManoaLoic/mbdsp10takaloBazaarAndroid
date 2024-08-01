package com.mustfaibra.roffu.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mustfaibra.roffu.utils.getFormattedDate
import java.util.*

data class Object (
    @Expose
    @SerializedName("id")
    val id: Int,

    @Expose
    @SerializedName("name")
    val name: String,

    @Expose
    @SerializedName("description")
    val description: String,

    @Expose
    @SerializedName("updated_at:")
    val updated_at: String = Date().getFormattedDate("yyyy-MM-dd HH:mm"),

    @Expose
    @SerializedName("created_at")
    val created_at: String = Date().getFormattedDate("yyyy-MM-dd HH:mm"),

    @Expose
    @SerializedName("status")
    val status: String,

    @Expose
    @SerializedName("user_id")
    val user_id: Int,

    @Expose
    @SerializedName("category_id")
    val category_id: Int,

    @Expose
    @SerializedName("deleted_at")
    val deletedAt: String = Date().getFormattedDate("yyyy-MM-dd HH:mm"),
)
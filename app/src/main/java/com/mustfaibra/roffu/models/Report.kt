package com.mustfaibra.roffu.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mustfaibra.roffu.utils.getFormattedDate
import java.util.*

data class Report (
    @Expose
    @SerializedName("id")
    val id: Int,

    @Expose
    @SerializedName("reporter_user_id")
    val reporter_user_id: Int,

    @Expose
    @SerializedName("object_id")
    val object_id: Int,

    @Expose
    @SerializedName("reason")
    val reason: String,

    @Expose
    @SerializedName("created_at")
    val created_at: String = Date().getFormattedDate("yyyy-MM-dd HH:mm"),
)
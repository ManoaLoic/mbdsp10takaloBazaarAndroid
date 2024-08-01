package com.mustfaibra.roffu.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mustfaibra.roffu.utils.getFormattedDate
import java.util.*

data class Exchange (
    @Expose
    @SerializedName("id")
    val id: Int,

    @Expose
    @SerializedName("proposerUserId")
    val proposerUserId: Int,

    @Expose
    @SerializedName("receiverUserId")
    val receiverUserId: Int,

    @Expose
    @SerializedName("status")
    val status: String,

    @Expose
    @SerializedName("note")
    val note: String,

    @Expose
    @SerializedName("appointmentDate")
    val appointmentDate: String = Date().getFormattedDate("yyyy-MM-dd HH:mm"),

    @Expose
    @SerializedName("meetingPlace")
    val meetingPlace: String,

    @Expose
    @SerializedName("date")
    val date: String = Date().getFormattedDate("yyyy-MM-dd HH:mm"),

    @Expose
    @SerializedName("updated_at:")
    val updated_at: String = Date().getFormattedDate("yyyy-MM-dd HH:mm"),

    @Expose
    @SerializedName("created_at")
    val created_at: String = Date().getFormattedDate("yyyy-MM-dd HH:mm"),
)
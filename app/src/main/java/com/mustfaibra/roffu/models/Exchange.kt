package com.mustfaibra.roffu.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mustfaibra.roffu.utils.getFormattedDate
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Exchange(
    @Expose
    @SerializedName("id")
    val id: Int,

    @Expose
    @SerializedName("proposer_user_id")
    val proposerUserId: Int,

    @Expose
    @SerializedName("receiver_user_id")
    val receiverUserId: Int,

    @Expose
    @SerializedName("status")
    private val _status: String,

    @Expose
    @SerializedName("note")
    val note: String?,

    @Expose
    @SerializedName("appointment_date")
    val appointmentDate: String? = null,

    @Expose
    @SerializedName("meeting_place")
    val meetingPlace: String? = null,

    @Expose
    @SerializedName("date")
    val date: String? = null,

    @Expose
    @SerializedName("updated_at")
    val updatedAt: String = Date().getFormattedDate("yyyy-MM-dd HH:mm"),

    @Expose
    @SerializedName("created_at")
    val createdAt: String = Date().getFormattedDate("yyyy-MM-dd HH:mm"),

    @Expose
    @SerializedName("proposer")
    val proposer: CustomUser?,

    @Expose
    @SerializedName("receiver")
    val receiver: CustomUser?,

    @Expose
    @SerializedName("exchange_objects")
    val exchangeObjects: List<ExchangeObject>?
){
    val status: String
        get() = when (_status) {
            "Proposed" -> "Proposé"
            "Accepted" -> "Accepté"
            "Refused" -> "Refusé"
            "Cancelled" -> "Annulé"
            else -> _status
        }
}

@Serializable
data class ExchangeObject(
    @Expose
    @SerializedName("id")
    val id: Int,

    @Expose
    @SerializedName("exchange_id")
    val exchangeId: Int,

    @Expose
    @SerializedName("object_id")
    val objectId: Int,

    @Expose
    @SerializedName("user_id")
    val userId: Int,

    @Expose
    @SerializedName("object")
    val obj: Object,
)

data class ExchangeResponse(
    @Expose
    @SerializedName("data")
    val data: List<Exchange>
)

@Serializable
data class ProposeExchangeRequest(
    val rcvUserId: Int,
    val rcvObjectId: List<Int>,
    val prpObjectId: List<Int>
)

@Serializable
data class ErrorResponse(
    val message: String,
    val error: String
)

@Serializable
data class CreateResponse(
    val message: String,
    val exchange: Exchange
)
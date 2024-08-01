package com.mustfaibra.roffu.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ExchangeObject (
    @Expose
    @SerializedName("id")
    val id: Int,

    @Expose
    @SerializedName("exchange_id")
    val exchange_id: Int,

    @Expose
    @SerializedName("object_id")
    val object_id: Int,

    @Expose
    @SerializedName("user_id")
    val user_id: Int,

)
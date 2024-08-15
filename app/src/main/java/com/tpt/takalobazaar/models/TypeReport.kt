package com.tpt.takalobazaar.models

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ReportRequest(
    @SerializedName("object_id") val objectId: Int,
    @SerializedName("reason") val reason: String
)

@Serializable
data class ReportResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("object_id") val objectId: Int,
    @SerializedName("reporter_user_id") val reporterUserId: Int,
    @SerializedName("reason") val reason: String,
    @SerializedName("created_at") val createdAt: String
)

@Serializable
data class TypeReport(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

@Serializable
data class TypeReportsResponse(
    @SerializedName("typeReports") val typeReports: List<TypeReport>,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("currentPage") val currentPage: Int
)

@Serializable
data class TypeReportsListResponse(
    @SerializedName("data") val data: TypeReportsResponse
)

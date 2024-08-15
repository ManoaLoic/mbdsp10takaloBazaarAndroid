package com.tpt.takalobazaar.api

import com.tpt.takalobazaar.models.ReportRequest
import com.tpt.takalobazaar.models.ReportResponse
import com.tpt.takalobazaar.models.TypeReportsListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ReportService {

    @GET("typeReports")
    suspend fun getTypeReport(): Response<TypeReportsListResponse>

    @POST("reports")
    suspend fun reportObject(
        @Body reportRequest: ReportRequest
    ): Response<ReportResponse>

}

package com.tpt.takalobazaar.api

import com.tpt.takalobazaar.models.CreateResponse
import com.tpt.takalobazaar.models.Exchange
import com.tpt.takalobazaar.models.ExchangeHistoryResponse
import com.tpt.takalobazaar.models.ExchangeResponse
import com.tpt.takalobazaar.models.ProposeExchangeRequest
import retrofit2.Response
import retrofit2.http.*

interface ExchangeService {

    @GET("exchange/{exchangeId}")
    suspend fun getExchangeById(@Path("exchangeId") exchangeId: String): Response<Exchange>

    @POST("exchange/proposed")
    suspend fun proposerExchange(@Body body: ProposeExchangeRequest): Response<CreateResponse>

    @PATCH("exchange/{exchangeId}/accept")
    suspend fun acceptExchange(@Path("exchangeId") exchangeId: String, @Body body: Any): Response<Any>

    @PATCH("exchange/{exchangeId}/reject")
    suspend fun rejectExchange(@Path("exchangeId") exchangeId: String, @Body note: Map<String, String>): Response<Any>

    @GET("exchange/history/{userId}")
    suspend fun getExchangeHistory(
        @Path("userId") userId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("status") status: String = ""
    ): Response<ExchangeHistoryResponse>

    @GET("exchanges/myCurrents")
    suspend fun myCurrentExchange(): Response<ExchangeResponse>
}

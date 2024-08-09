package com.mustfaibra.roffu.api

import com.mustfaibra.roffu.models.Exchange
import com.mustfaibra.roffu.models.ExchangeResponse
import retrofit2.Response
import retrofit2.http.*

interface ExchangeService {

    @GET("exchange/{exchangeId}")
    suspend fun getExchangeById(@Path("exchangeId") exchangeId: String): Response<Exchange>

    @POST("exchange/proposed")
    suspend fun proposerExchange(@Body body: Any): Response<Any>

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
    ): Response<Any>

    @GET("exchanges/myCurrents")
    suspend fun myCurrentExchange(): Response<ExchangeResponse>
}

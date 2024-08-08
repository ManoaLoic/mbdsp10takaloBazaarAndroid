package com.mustfaibra.roffu.api

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.mustfaibra.roffu.services.SessionService
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private var token: String? = """"""
    private var onUnauthorizedCallback: (() -> Unit)? = null
    private lateinit var sessionService: SessionService
    private val mainHandler = Handler(Looper.getMainLooper())

    fun initialize(sessionService: SessionService) {
        this.sessionService = sessionService
        sessionService.fetchUserSynchronously()
        token = sessionService.getCachedToken()
    }

    fun setToken(newToken: String) {
        token = newToken
    }

    fun setOnUnauthorizedCallback(callback: () -> Unit) {
        onUnauthorizedCallback = {
            mainHandler.post(callback)
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${token ?: ""}")
                    .build()
                Log.d("RetrofitInstance", "Authorization Header: ${newRequest.header("Authorization")}")
                val response = chain.proceed(newRequest)
                if (response.code == 401) {
                    onUnauthorizedCallback?.invoke()
                }
                response
            }
            .addInterceptor(loggingInterceptor)
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://takalobazarserver.onrender.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}

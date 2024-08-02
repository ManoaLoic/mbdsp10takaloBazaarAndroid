package com.mustfaibra.roffu.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private var token: String? = """eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImVtYXJ0aW5AZXhhbXBsZS5jb20iLCJpZCI6NTUsImZpcnN0X25hbWUiOiJFbW1hIG1vZGlmIiwibGFzdF9uYW1lIjoiRW1tYSIsInVzZXJuYW1lIjoiZW1hcnRpbiIsInR5cGUiOiJVU0VSIiwianRpIjoiNTUtMTcyMjYzNzI0MTQxNiIsImlhdCI6MTcyMjYzNzI0MSwiZXhwIjoxNzIyODEwMDQxfQ.-irrIDhxBRpRXfnKMQnQlnkQyeZ1i0HvoYTMw6twcw0"""

    fun setToken(newToken: String) {
        token = newToken
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
                chain.proceed(newRequest)
            }
            .addInterceptor(loggingInterceptor)
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

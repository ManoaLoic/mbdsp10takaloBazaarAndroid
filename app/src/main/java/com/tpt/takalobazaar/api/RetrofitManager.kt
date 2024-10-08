package com.tpt.takalobazaar.api

import com.tpt.takalobazaar.services.SessionService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitManager @Inject constructor(
    private val sessionService: SessionService
) {

    init {
        RetrofitInstance.initialize(sessionService)
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return RetrofitInstance.createService(serviceClass)
    }
}

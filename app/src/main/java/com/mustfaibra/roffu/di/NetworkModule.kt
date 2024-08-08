package com.mustfaibra.roffu.di

import android.content.Context
import com.mustfaibra.roffu.api.ExchangeService
import com.mustfaibra.roffu.api.ObjectService
import com.mustfaibra.roffu.api.RetrofitInstance
import com.mustfaibra.roffu.api.RetrofitManager
import com.mustfaibra.roffu.services.SessionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSessionService(@ApplicationContext context: Context): SessionService {
        return SessionService(context)
    }

    @Provides
    @Singleton
    fun provideObjectService(): ObjectService {
        return RetrofitInstance.createService(ObjectService::class.java)
    }

    @Provides
    @Singleton
    fun provideExchangeService(): ExchangeService {
        return RetrofitInstance.createService(ExchangeService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofitManager(sessionService: SessionService): RetrofitManager {
        return RetrofitManager(sessionService)
    }

}

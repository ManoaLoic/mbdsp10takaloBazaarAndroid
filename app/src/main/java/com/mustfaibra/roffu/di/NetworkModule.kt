package com.mustfaibra.roffu.di

import com.mustfaibra.roffu.api.ObjectService
import com.mustfaibra.roffu.api.RetrofitInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(): RetrofitInstance {
        return RetrofitInstance
    }

    @Provides
    @Singleton
    fun provideObjectService(retrofitInstance: RetrofitInstance): ObjectService {
        return retrofitInstance.createService(ObjectService::class.java)
    }
}

package com.upper.trading.di

import com.upper.trading.data.local.datasource.AuthLocalDataSource
import com.upper.trading.data.local.datasource.AuthLocalDataSourceImpl
import com.upper.trading.data.remote.datasource.AuthRemoteDataSource
import com.upper.trading.data.remote.datasource.AuthRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        impl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAuthLocalDataSource(
        impl: AuthLocalDataSourceImpl
    ): AuthLocalDataSource
}

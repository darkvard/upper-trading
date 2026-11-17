package com.upper.trading.di

import jakarta.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserSessionDataStore
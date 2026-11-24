package com.upper.trading.di

import android.content.Context
import androidx.datastore.core.DataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import com.upper.trading.data.local.datastore.AppDataStorageImpl
import com.upper.trading.data.local.datastore.UserSessionDataStorageImpl
import com.upper.trading.di.qualifier.AppDataStore
import com.upper.trading.di.qualifier.UserSessionDataStore
import com.upper.trading.domain.storage.AppDataStorage
import com.upper.trading.domain.storage.UserSessionDataStorage
import dagger.Binds
import javax.inject.Singleton

private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_preferences"
)

private val Context.userSessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_session_preferences"
)

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStorageModule {

    @Binds
    @Singleton
    abstract fun bindAppDataStorage(impl: AppDataStorageImpl): AppDataStorage

    @Binds
    @Singleton
    abstract fun bindUserSessionDataStorage(impl: UserSessionDataStorageImpl): UserSessionDataStorage

    companion object {
        @Provides
        @Singleton
        @AppDataStore
        fun provideAppDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return context.appDataStore
        }

        @Provides
        @Singleton
        @UserSessionDataStore
        fun provideUserSessionDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
            return context.userSessionDataStore
        }
    }
}
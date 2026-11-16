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
import com.upper.trading.data.storage.UserPreferencesDataSourceImpl
import com.upper.trading.domain.storage.UserPreferencesDataSource
import dagger.Binds
import javax.inject.Singleton

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStorageModule {

    @Binds
    @Singleton
    abstract fun bindUserPreferencesDataSource(
        impl: UserPreferencesDataSourceImpl
    ): UserPreferencesDataSource

    companion object {
        @Provides
        @Singleton
        fun provideDataStorage(@ApplicationContext context: Context): DataStore<Preferences> {
            return context.userDataStore
        }
    }
}
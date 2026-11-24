package com.upper.trading

import android.app.Application
import com.upper.trading.data.config.RemoteConfigManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application() {

    @Inject
    lateinit var remoteConfigManager: RemoteConfigManager

    override fun onCreate() {
        super.onCreate()
        remoteConfigManager.init()
    }
}

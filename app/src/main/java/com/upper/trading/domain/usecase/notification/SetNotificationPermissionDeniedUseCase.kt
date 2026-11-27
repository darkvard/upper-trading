package com.upper.trading.domain.usecase.notification

import com.upper.trading.domain.storage.AppDataStorage
import javax.inject.Inject

class SetNotificationPermissionDeniedUseCase @Inject constructor(
    private val dataStorage: AppDataStorage
) {

    suspend operator fun invoke(denied: Boolean = true) {
        dataStorage.setNotificationPermissionDenied(denied)
    }
}

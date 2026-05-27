package com.yourname.privatevault.data.repository

import android.content.Context
import com.yourname.privatevault.data.dao.AppSettingsDao
import com.yourname.privatevault.data.entity.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

class SettingsRepository(context: Context, private val appSettingsDao: AppSettingsDao) {
    val settings: Flow<AppSettingsEntity?> = appSettingsDao.getSettings()

    suspend fun getSettingsSync(): AppSettingsEntity? {
        return appSettingsDao.getSettingsSync()
    }

    suspend fun setPin(pin: String?) {
        val current = getSettingsSync()
        val settings = current?.copy(authPin = pin) ?: AppSettingsEntity(authPin = pin)
        appSettingsDao.insert(settings)
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        val current = getSettingsSync()
        val settings = current?.copy(useBiometric = enabled) ?: AppSettingsEntity(useBiometric = enabled)
        appSettingsDao.insert(settings)
    }

    suspend fun updateLastBackupTime() {
        val current = getSettingsSync()
        val settings = current?.copy(lastBackupAt = System.currentTimeMillis()) ?: AppSettingsEntity(lastBackupAt = System.currentTimeMillis())
        appSettingsDao.insert(settings)
    }
}

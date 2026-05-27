package com.yourname.privatevault.data.dao

import androidx.room.*
import com.yourname.privatevault.data.entity.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun getSettingsSync(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: AppSettingsEntity)

    @Update
    suspend fun update(settings: AppSettingsEntity)

    @Delete
    suspend fun delete(settings: AppSettingsEntity)
}

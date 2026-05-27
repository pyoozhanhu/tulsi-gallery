package com.yourname.privatevault.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * App 设置实体
 * @param id 固定为 1
 * @param authPin PIN 码
 * @param useBiometric 是否使用生物识别
 * @param lastBackupAt 最后备份时间
 */
@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val authPin: String? = null,
    val useBiometric: Boolean = true,
    val lastBackupAt: Long = 0L
)

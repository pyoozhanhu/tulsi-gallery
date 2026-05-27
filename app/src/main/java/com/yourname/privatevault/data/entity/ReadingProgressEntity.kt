package com.yourname.privatevault.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 阅读进度实体
 * @param seriesId 漫画集 ID（主键）
 * @param currentPage 当前页码
 * @param totalPages 总页数
 * @param lastReadAt 最后阅读时间
 */
@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey val seriesId: Long,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val lastReadAt: Long = System.currentTimeMillis()
)

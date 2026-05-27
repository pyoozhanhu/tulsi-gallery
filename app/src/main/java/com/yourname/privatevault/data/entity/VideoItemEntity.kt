package com.yourname.privatevault.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 视频条目实体
 * @param id 唯一标识
 * @param albumId 所属视频集 ID
 * @param filePath 视频文件路径
 * @param thumbnailPath 缩略图路径
 * @param fileName 原始文件名
 * @param duration 时长（毫秒）
 * @param addedAt 添加时间
 */
@Entity(
    tableName = "video_items",
    foreignKeys = [ForeignKey(
        entity = VideoAlbumEntity::class,
        parentColumns = ["id"],
        childColumns = ["albumId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("albumId")]
)
data class VideoItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val albumId: Long,
    val filePath: String,
    val thumbnailPath: String?,
    val fileName: String,
    val duration: Long = 0,
    val addedAt: Long = System.currentTimeMillis()
)

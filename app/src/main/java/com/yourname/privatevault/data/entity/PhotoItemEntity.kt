package com.yourname.privatevault.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 图片条目实体
 * @param id 唯一标识
 * @param albumId 所属相册 ID
 * @param filePath 图片路径
 * @param fileName 原始文件名
 * @param addedAt 添加时间
 */
@Entity(
    tableName = "photo_items",
    foreignKeys = [ForeignKey(
        entity = PhotoAlbumEntity::class,
        parentColumns = ["id"],
        childColumns = ["albumId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("albumId")]
)
data class PhotoItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val albumId: Long,
    val filePath: String,
    val fileName: String,
    val addedAt: Long = System.currentTimeMillis()
)

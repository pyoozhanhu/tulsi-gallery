package com.yourname.privatevault.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 视频分类实体（视频的"漫画集"等价物）
 * @param id 唯一标识
 * @param folderId 所属折叠框 ID
 * @param name 视频集名称
 * @param coverPath 封面缩略图路径
 * @param videoCount 视频数量
 * @param sortOrder 排序
 * @param createdAt 创建时间
 */
@Entity(
    tableName = "video_albums",
    foreignKeys = [ForeignKey(
        entity = FolderEntity::class,
        parentColumns = ["id"],
        childColumns = ["folderId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("folderId")]
)
data class VideoAlbumEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val folderId: Long,
    val name: String,
    val coverPath: String?,
    val videoCount: Int = 0,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

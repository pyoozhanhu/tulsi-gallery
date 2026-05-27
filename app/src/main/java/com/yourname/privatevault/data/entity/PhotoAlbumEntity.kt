package com.yourname.privatevault.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 图片相册实体
 * @param id 唯一标识
 * @param folderId 所属折叠框 ID
 * @param name 相册名称
 * @param coverPath 封面路径
 * @param photoCount 照片数量
 * @param sortOrder 排序
 * @param createdAt 创建时间
 */
@Entity(
    tableName = "photo_albums",
    foreignKeys = [ForeignKey(
        entity = FolderEntity::class,
        parentColumns = ["id"],
        childColumns = ["folderId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("folderId")]
)
data class PhotoAlbumEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val folderId: Long,
    val name: String,
    val coverPath: String?,
    val photoCount: Int = 0,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

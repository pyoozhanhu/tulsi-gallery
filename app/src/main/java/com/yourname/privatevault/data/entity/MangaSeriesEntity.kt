package com.yourname.privatevault.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 漫画集实体
 * @param id 唯一标识
 * @param folderId 所属折叠框 ID
 * @param name 漫画集名称（如"海贼王"）
 * @param coverPath 封面（第一页路径）
 * @param pageCount 总页数
 * @param sortOrder 排序
 * @param createdAt 创建时间
 */
@Entity(
    tableName = "manga_series",
    foreignKeys = [ForeignKey(
        entity = FolderEntity::class,
        parentColumns = ["id"],
        childColumns = ["folderId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("folderId")]
)
data class MangaSeriesEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val folderId: Long,
    val name: String,
    val coverPath: String?,
    val pageCount: Int = 0,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

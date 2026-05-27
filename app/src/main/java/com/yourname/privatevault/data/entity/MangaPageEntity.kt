package com.yourname.privatevault.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 漫画页面实体（章节内的每一页）
 * @param id 唯一标识
 * @param seriesId 所属漫画集 ID
 * @param filePath 图片路径
 * @param pageNumber 页码（文件名前缀数字）
 * @param fileName 原始文件名
 */
@Entity(
    tableName = "manga_pages",
    foreignKeys = [ForeignKey(
        entity = MangaSeriesEntity::class,
        parentColumns = ["id"],
        childColumns = ["seriesId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("seriesId")]
)
data class MangaPageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val seriesId: Long,
    val filePath: String,
    val pageNumber: Int,
    val fileName: String
)

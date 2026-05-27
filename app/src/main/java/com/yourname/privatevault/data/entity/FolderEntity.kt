package com.yourname.privatevault.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 折叠框实体 - 图片/漫画/视频三个板块通用
 * @param id 唯一标识
 * @param name 折叠框名称（如"少年漫画"）
 * @param type "photo" / "manga" / "video"
 * @param sortOrder 排序
 * @param createdAt 创建时间
 */
@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

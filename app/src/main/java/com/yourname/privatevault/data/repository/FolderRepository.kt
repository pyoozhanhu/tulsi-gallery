package com.yourname.privatevault.data.repository

import com.yourname.privatevault.data.dao.FolderDao
import com.yourname.privatevault.data.entity.FolderEntity
import kotlinx.coroutines.flow.Flow

class FolderRepository(private val folderDao: FolderDao) {
    val mangaFolders: Flow<List<FolderEntity>> = folderDao.getFoldersByType("manga")
    val videoFolders: Flow<List<FolderEntity>> = folderDao.getFoldersByType("video")
    val photoFolders: Flow<List<FolderEntity>> = folderDao.getFoldersByType("photo")

    fun getFolderById(id: Long): Flow<FolderEntity?> = folderDao.getFolderByIdFlow(id)

    suspend fun insert(name: String, type: String): Long {
        val maxSortOrder = folderDao.getMaxSortOrder(type) ?: 0
        val folder = FolderEntity(
            name = name,
            type = type,
            sortOrder = maxSortOrder + 1
        )
        return folderDao.insert(folder)
    }

    suspend fun update(folder: FolderEntity) {
        folderDao.update(folder)
    }

    suspend fun delete(folder: FolderEntity) {
        folderDao.delete(folder)
    }

    suspend fun deleteById(id: Long) {
        folderDao.deleteById(id)
    }
}

package com.yourname.privatevault.data.repository

import com.yourname.privatevault.data.dao.PhotoAlbumDao
import com.yourname.privatevault.data.dao.PhotoItemDao
import com.yourname.privatevault.data.entity.PhotoAlbumEntity
import com.yourname.privatevault.data.entity.PhotoItemEntity
import kotlinx.coroutines.flow.Flow

class PhotoRepository(
    private val photoAlbumDao: PhotoAlbumDao,
    private val photoItemDao: PhotoItemDao
) {
    fun getAlbumsByFolder(folderId: Long): Flow<List<PhotoAlbumEntity>> =
        photoAlbumDao.getAlbumsByFolder(folderId)

    fun getItemsByAlbum(albumId: Long): Flow<List<PhotoItemEntity>> =
        photoItemDao.getItemsByAlbum(albumId)

    suspend fun insertAlbum(folderId: Long, name: String): Long {
        val maxSortOrder = photoAlbumDao.getMaxSortOrder(folderId) ?: 0
        val album = PhotoAlbumEntity(
            folderId = folderId,
            name = name,
            sortOrder = maxSortOrder + 1
        )
        return photoAlbumDao.insert(album)
    }

    suspend fun updateAlbum(album: PhotoAlbumEntity) {
        photoAlbumDao.update(album)
    }

    suspend fun deleteAlbum(album: PhotoAlbumEntity) {
        photoAlbumDao.delete(album)
    }

    suspend fun insertItem(albumId: Long, filePath: String, fileName: String): Long {
        val item = PhotoItemEntity(
            albumId = albumId,
            filePath = filePath,
            fileName = fileName,
            addedAt = System.currentTimeMillis()
        )
        return photoItemDao.insert(item)
    }

    suspend fun insertAllItems(items: List<PhotoItemEntity>) {
        photoItemDao.insertAll(items)
    }

    suspend fun deleteItemsByAlbum(albumId: Long) {
        photoItemDao.deleteAllByAlbum(albumId)
    }

    suspend fun deleteItemsByIds(ids: List<Long>) {
        photoItemDao.deleteByIds(ids)
    }

    suspend fun deleteItemById(id: Long) {
        photoItemDao.deleteById(id)
    }

    suspend fun updateItem(id: Long, newName: String? = null, newFilePath: String? = null) {
        val item = photoItemDao.getById(id) ?: return
        val updatedItem = item.copy(
            fileName = newName ?: item.fileName,
            filePath = newFilePath ?: item.filePath
        )
        photoItemDao.update(updatedItem)
    }
}

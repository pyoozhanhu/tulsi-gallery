package com.yourname.privatevault.data.repository

import com.yourname.privatevault.data.dao.VideoAlbumDao
import com.yourname.privatevault.data.dao.VideoItemDao
import com.yourname.privatevault.data.entity.VideoAlbumEntity
import com.yourname.privatevault.data.entity.VideoItemEntity
import kotlinx.coroutines.flow.Flow

class VideoRepository(
    private val videoAlbumDao: VideoAlbumDao,
    private val videoItemDao: VideoItemDao
) {
    fun getAlbumsByFolder(folderId: Long): Flow<List<VideoAlbumEntity>> =
        videoAlbumDao.getAlbumsByFolder(folderId)

    fun getItemsByAlbum(albumId: Long): Flow<List<VideoItemEntity>> =
        videoItemDao.getItemsByAlbum(albumId)

    suspend fun insertAlbum(folderId: Long, name: String): Long {
        val maxSortOrder = videoAlbumDao.getMaxSortOrder(folderId) ?: 0
        val album = VideoAlbumEntity(
            folderId = folderId,
            name = name,
            sortOrder = maxSortOrder + 1
        )
        return videoAlbumDao.insert(album)
    }

    suspend fun updateAlbum(album: VideoAlbumEntity) {
        videoAlbumDao.update(album)
    }

    suspend fun deleteAlbum(album: VideoAlbumEntity) {
        videoAlbumDao.delete(album)
    }

    suspend fun insertItem(albumId: Long, filePath: String, fileName: String, duration: Long = 0, thumbnailPath: String? = null): Long {
        val item = VideoItemEntity(
            albumId = albumId,
            filePath = filePath,
            fileName = fileName,
            duration = duration,
            thumbnailPath = thumbnailPath,
            addedAt = System.currentTimeMillis()
        )
        return videoItemDao.insert(item)
    }

    suspend fun insertAllItems(items: List<VideoItemEntity>) {
        videoItemDao.insertAll(items)
    }

    suspend fun deleteItemsByAlbum(albumId: Long) {
        videoItemDao.deleteAllByAlbum(albumId)
    }

    suspend fun deleteItemsByIds(ids: List<Long>) {
        videoItemDao.deleteByIds(ids)
    }

    suspend fun deleteItemById(id: Long) {
        videoItemDao.deleteById(id)
    }

    suspend fun updateItem(id: Long, newName: String? = null, newFilePath: String? = null) {
        val item = videoItemDao.getById(id) ?: return
        val updatedItem = item.copy(
            fileName = newName ?: item.fileName,
            filePath = newFilePath ?: item.filePath
        )
        videoItemDao.update(updatedItem)
    }
}

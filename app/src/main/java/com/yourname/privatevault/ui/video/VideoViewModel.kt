package com.yourname.privatevault.ui.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yourname.privatevault.data.entity.FolderEntity
import com.yourname.privatevault.data.entity.VideoAlbumEntity
import com.yourname.privatevault.data.entity.VideoItemEntity
import com.yourname.privatevault.data.repository.FolderRepository
import com.yourname.privatevault.data.repository.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * 视频板块 ViewModel
 */
class VideoViewModel(
    private val folderRepository: FolderRepository,
    private val videoRepository: VideoRepository
) : ViewModel() {

    fun getFolders(): Flow<List<FolderEntity>> = folderRepository.videoFolders

    fun getAlbumsByFolder(folderId: Long): Flow<List<VideoAlbumEntity>> =
        videoRepository.getAlbumsByFolder(folderId)

    fun getItemsByAlbum(albumId: Long): Flow<List<VideoItemEntity>> =
        videoRepository.getItemsByAlbum(albumId)

    fun createFolder(name: String) = viewModelScope.launch {
        folderRepository.insert(name, "video")
    }

    fun updateFolder(folder: FolderEntity) = viewModelScope.launch {
        folderRepository.update(folder)
    }

    fun deleteFolder(folder: FolderEntity) = viewModelScope.launch {
        folderRepository.delete(folder)
    }

    fun createAlbum(folderId: Long, name: String) = viewModelScope.launch {
        videoRepository.insertAlbum(folderId, name)
    }

    fun updateAlbum(album: VideoAlbumEntity) = viewModelScope.launch {
        videoRepository.updateAlbum(album)
    }

    fun deleteAlbum(album: VideoAlbumEntity) = viewModelScope.launch {
        videoRepository.deleteAlbum(album)
    }

    fun insertItem(albumId: Long, filePath: String, fileName: String, duration: Long = 0) = viewModelScope.launch {
        videoRepository.insertItem(albumId, filePath, fileName, duration)
    }

    fun insertAllItems(items: List<VideoItemEntity>) = viewModelScope.launch {
        videoRepository.insertAllItems(items)
    }

    fun deleteItemsByAlbum(albumId: Long) = viewModelScope.launch {
        videoRepository.deleteItemsByAlbum(albumId)
    }

    fun deleteItemsByIds(ids: List<Long>) = viewModelScope.launch {
        videoRepository.deleteItemsByIds(ids)
    }

    fun deleteItem(id: Long) = viewModelScope.launch {
        videoRepository.deleteItemById(id)
    }

    fun updateItem(id: Long, newName: String? = null, newFilePath: String? = null) = viewModelScope.launch {
        videoRepository.updateItem(id, newName, newFilePath)
    }

    /**
     * ViewModel Factory
     */
    class Factory(
        private val folderRepository: FolderRepository,
        private val videoRepository: VideoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VideoViewModel::class.java)) {
                return VideoViewModel(folderRepository, videoRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

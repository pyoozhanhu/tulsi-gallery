package com.yourname.privatevault.ui.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yourname.privatevault.data.entity.FolderEntity
import com.yourname.privatevault.data.entity.PhotoAlbumEntity
import com.yourname.privatevault.data.entity.PhotoItemEntity
import com.yourname.privatevault.data.repository.FolderRepository
import com.yourname.privatevault.data.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * 图片板块 ViewModel
 */
class PhotoViewModel(
    private val folderRepository: FolderRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    fun getFolders(): Flow<List<FolderEntity>> = folderRepository.photoFolders

    fun getAlbumsByFolder(folderId: Long): Flow<List<PhotoAlbumEntity>> =
        photoRepository.getAlbumsByFolder(folderId)

    fun getItemsByAlbum(albumId: Long): Flow<List<PhotoItemEntity>> =
        photoRepository.getItemsByAlbum(albumId)

    fun createFolder(name: String) = viewModelScope.launch {
        folderRepository.insert(name, "photo")
    }

    fun updateFolder(folder: FolderEntity) = viewModelScope.launch {
        folderRepository.update(folder)
    }

    fun deleteFolder(folder: FolderEntity) = viewModelScope.launch {
        folderRepository.delete(folder)
    }

    fun createAlbum(folderId: Long, name: String) = viewModelScope.launch {
        photoRepository.insertAlbum(folderId, name)
    }

    fun updateAlbum(album: PhotoAlbumEntity) = viewModelScope.launch {
        photoRepository.updateAlbum(album)
    }

    fun deleteAlbum(album: PhotoAlbumEntity) = viewModelScope.launch {
        photoRepository.deleteAlbum(album)
    }

    fun insertItem(albumId: Long, filePath: String, fileName: String) = viewModelScope.launch {
        photoRepository.insertItem(albumId, filePath, fileName)
    }

    fun insertAllItems(items: List<PhotoItemEntity>) = viewModelScope.launch {
        photoRepository.insertAllItems(items)
    }

    fun deleteItemsByAlbum(albumId: Long) = viewModelScope.launch {
        photoRepository.deleteItemsByAlbum(albumId)
    }

    fun deleteItemsByIds(ids: List<Long>) = viewModelScope.launch {
        photoRepository.deleteItemsByIds(ids)
    }

    fun deleteItem(id: Long) = viewModelScope.launch {
        photoRepository.deleteItemById(id)
    }

    fun updateItem(id: Long, newName: String? = null, newFilePath: String? = null) = viewModelScope.launch {
        photoRepository.updateItem(id, newName, newFilePath)
    }

    /**
     * ViewModel Factory
     */
    class Factory(
        private val folderRepository: FolderRepository,
        private val photoRepository: PhotoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PhotoViewModel::class.java)) {
                return PhotoViewModel(folderRepository, photoRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

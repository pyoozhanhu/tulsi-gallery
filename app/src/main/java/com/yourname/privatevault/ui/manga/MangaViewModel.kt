package com.yourname.privatevault.ui.manga

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yourname.privatevault.data.entity.FolderEntity
import com.yourname.privatevault.data.entity.MangaPageEntity
import com.yourname.privatevault.data.entity.MangaSeriesEntity
import com.yourname.privatevault.data.entity.ReadingProgressEntity
import com.yourname.privatevault.data.repository.FolderRepository
import com.yourname.privatevault.data.repository.MangaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 漫画板块 ViewModel
 */
class MangaViewModel(
    private val folderRepository: FolderRepository,
    private val mangaRepository: MangaRepository
) : ViewModel() {

    fun getFoldersByType(): Flow<List<FolderEntity>> = folderRepository.mangaFolders

    fun getSeriesByFolder(folderId: Long): Flow<List<MangaSeriesEntity>> =
        mangaRepository.getSeriesByFolder(folderId)

    fun getSeriesById(seriesId: Long): Flow<MangaSeriesEntity?> =
        mangaRepository.getSeriesById(seriesId)

    fun getPagesBySeries(seriesId: Long): Flow<List<MangaPageEntity>> =
        mangaRepository.getPagesBySeries(seriesId)

    fun getProgressBySeries(seriesId: Long): Flow<ReadingProgressEntity?> =
        mangaRepository.getProgressBySeries(seriesId)

    fun createFolder(name: String) = viewModelScope.launch {
        folderRepository.insert(name, "manga")
    }

    fun updateFolder(folder: FolderEntity) = viewModelScope.launch {
        folderRepository.update(folder)
    }

    fun deleteFolder(folder: FolderEntity) = viewModelScope.launch {
        folderRepository.delete(folder)
    }

    fun createSeries(folderId: Long, name: String) = viewModelScope.launch {
        mangaRepository.insertSeries(folderId, name)
    }

    fun updateSeries(series: MangaSeriesEntity) = viewModelScope.launch {
        mangaRepository.updateSeries(series)
    }

    fun deleteSeries(series: MangaSeriesEntity) = viewModelScope.launch {
        mangaRepository.deleteSeries(series)
    }

    fun deleteFolderById(folderId: Long) = viewModelScope.launch {
        folderRepository.deleteById(folderId)
    }

    fun insertPages(pages: List<MangaPageEntity>) = viewModelScope.launch {
        mangaRepository.insertAllPages(pages)
    }

    fun deletePagesBySeries(seriesId: Long) = viewModelScope.launch {
        mangaRepository.deletePagesBySeries(seriesId)
    }

    fun deletePagesByIds(ids: List<Long>) = viewModelScope.launch {
        mangaRepository.deletePagesByIds(ids)
    }

    fun saveProgress(seriesId: Long, currentPage: Int, totalPages: Int) = viewModelScope.launch {
        mangaRepository.saveProgress(seriesId, currentPage, totalPages)
    }

    fun deleteProgress(seriesId: Long) = viewModelScope.launch {
        mangaRepository.deleteProgress(seriesId)
    }

    /**
     * ViewModel Factory
     */
    class Factory(
        private val folderRepository: FolderRepository,
        private val mangaRepository: MangaRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MangaViewModel::class.java)) {
                return MangaViewModel(folderRepository, mangaRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

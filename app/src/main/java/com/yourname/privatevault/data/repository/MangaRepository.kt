package com.yourname.privatevault.data.repository

import com.yourname.privatevault.data.dao.MangaPageDao
import com.yourname.privatevault.data.dao.MangaSeriesDao
import com.yourname.privatevault.data.dao.ReadingProgressDao
import com.yourname.privatevault.data.entity.MangaPageEntity
import com.yourname.privatevault.data.entity.MangaSeriesEntity
import com.yourname.privatevault.data.entity.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow

class MangaRepository(
    private val mangaSeriesDao: MangaSeriesDao,
    private val mangaPageDao: MangaPageDao,
    private val readingProgressDao: ReadingProgressDao
) {
    fun getSeriesByFolder(folderId: Long): Flow<List<MangaSeriesEntity>> =
        mangaSeriesDao.getSeriesByFolder(folderId)

    fun getSeriesById(id: Long): Flow<MangaSeriesEntity?> =
        mangaSeriesDao.getSeriesByFolderAndId(0, id)

    fun getPagesBySeries(seriesId: Long): Flow<List<MangaPageEntity>> =
        mangaPageDao.getPagesBySeries(seriesId)

    suspend fun insertSeries(folderId: Long, name: String): Long {
        val maxSortOrder = mangaSeriesDao.getMaxSortOrder(folderId) ?: 0
        val series = MangaSeriesEntity(
            folderId = folderId,
            name = name,
            sortOrder = maxSortOrder + 1
        )
        return mangaSeriesDao.insert(series)
    }

    suspend fun updateSeries(series: MangaSeriesEntity) {
        mangaSeriesDao.update(series)
    }

    suspend fun deleteSeries(series: MangaSeriesEntity) {
        mangaSeriesDao.delete(series)
    }

    suspend fun insertPage(seriesId: Long, filePath: String, pageNumber: Int, fileName: String): Long {
        val page = MangaPageEntity(
            seriesId = seriesId,
            filePath = filePath,
            pageNumber = pageNumber,
            fileName = fileName
        )
        return mangaPageDao.insert(page)
    }

    suspend fun insertAllPages(pages: List<MangaPageEntity>) {
        mangaPageDao.insertAll(pages)
    }

    suspend fun deletePagesBySeries(seriesId: Long) {
        mangaPageDao.deleteAllBySeries(seriesId)
    }

    suspend fun deletePagesByIds(ids: List<Long>) {
        mangaPageDao.deleteByIds(ids)
    }

    fun getProgressBySeries(seriesId: Long): Flow<ReadingProgressEntity?> =
        readingProgressDao.getProgressBySeries(seriesId)

    suspend fun saveProgress(seriesId: Long, currentPage: Int, totalPages: Int) {
        val progress = ReadingProgressEntity(
            seriesId = seriesId,
            currentPage = currentPage,
            totalPages = totalPages
        )
        readingProgressDao.insert(progress)
    }

    suspend fun deleteProgress(seriesId: Long) {
        readingProgressDao.deleteBySeries(seriesId)
    }
}

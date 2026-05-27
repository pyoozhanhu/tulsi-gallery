package com.yourname.privatevault.data.dao

import androidx.room.*
import com.yourname.privatevault.data.entity.MangaSeriesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaSeriesDao {
    @Query("SELECT * FROM manga_series WHERE folderId = :folderId ORDER BY sortOrder, createdAt")
    fun getSeriesByFolder(folderId: Long): Flow<List<MangaSeriesEntity>>

    @Query("SELECT * FROM manga_series WHERE id = :id")
    suspend fun getSeriesById(id: Long): MangaSeriesEntity?

    @Query("SELECT * FROM manga_series WHERE folderId = :folderId AND id = :seriesId")
    fun getSeriesByFolderAndId(folderId: Long, seriesId: Long): Flow<MangaSeriesEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(series: MangaSeriesEntity): Long

    @Update
    suspend fun update(series: MangaSeriesEntity)

    @Delete
    suspend fun delete(series: MangaSeriesEntity)

    @Query("DELETE FROM manga_series WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE manga_series SET pageCount = :count WHERE id = :id")
    suspend fun updatePageCount(id: Long, count: Int)

    @Query("UPDATE manga_series SET coverPath = :path WHERE id = :id")
    suspend fun updateCoverPath(id: Long, path: String?)

    @Query("SELECT MAX(sortOrder) FROM manga_series WHERE folderId = :folderId")
    suspend fun getMaxSortOrder(folderId: Long): Int?
}

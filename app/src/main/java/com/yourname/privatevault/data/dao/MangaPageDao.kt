package com.yourname.privatevault.data.dao

import androidx.room.*
import com.yourname.privatevault.data.entity.MangaPageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaPageDao {
    @Query("SELECT * FROM manga_pages WHERE seriesId = :seriesId ORDER BY pageNumber")
    fun getPagesBySeries(seriesId: Long): Flow<List<MangaPageEntity>>

    @Query("SELECT * FROM manga_pages WHERE seriesId = :seriesId ORDER BY pageNumber")
    suspend fun getPagesBySeriesSync(seriesId: Long): List<MangaPageEntity>

    @Query("SELECT * FROM manga_pages WHERE id = :id")
    suspend fun getPageById(id: Long): MangaPageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(page: MangaPageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pages: List<MangaPageEntity>)

    @Update
    suspend fun update(page: MangaPageEntity)

    @Delete
    suspend fun delete(page: MangaPageEntity)

    @Query("DELETE FROM manga_pages WHERE seriesId = :seriesId")
    suspend fun deleteAllBySeries(seriesId: Long)

    @Query("DELETE FROM manga_pages WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM manga_pages WHERE seriesId = :seriesId")
    suspend fun getCountBySeries(seriesId: Long): Int

    @Query("SELECT COUNT(*) FROM manga_pages WHERE seriesId = :seriesId")
    fun getCountBySeriesFlow(seriesId: Long): Flow<Int>
}

package com.yourname.privatevault.data.dao

import androidx.room.*
import com.yourname.privatevault.data.entity.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE seriesId = :seriesId")
    fun getProgressBySeries(seriesId: Long): Flow<ReadingProgressEntity?>

    @Query("SELECT * FROM reading_progress WHERE seriesId = :seriesId")
    suspend fun getProgressBySeriesSync(seriesId: Long): ReadingProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: ReadingProgressEntity)

    @Update
    suspend fun update(progress: ReadingProgressEntity)

    @Delete
    suspend fun delete(progress: ReadingProgressEntity)

    @Query("DELETE FROM reading_progress WHERE seriesId = :seriesId")
    suspend fun deleteBySeries(seriesId: Long)
}

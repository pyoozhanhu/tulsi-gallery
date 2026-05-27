package com.yourname.privatevault.data.dao

import androidx.room.*
import com.yourname.privatevault.data.entity.VideoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoItemDao {
    @Query("SELECT * FROM video_items WHERE albumId = :albumId ORDER BY addedAt")
    fun getItemsByAlbum(albumId: Long): Flow<List<VideoItemEntity>>

    @Query("SELECT * FROM video_items WHERE id = :id")
    suspend fun getItemById(id: Long): VideoItemEntity?

    @Query("DELETE FROM video_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: VideoItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<VideoItemEntity>)

    @Update
    suspend fun update(item: VideoItemEntity)

    @Delete
    suspend fun delete(item: VideoItemEntity)

    @Query("DELETE FROM video_items WHERE albumId = :albumId")
    suspend fun deleteAllByAlbum(albumId: Long)

    @Query("DELETE FROM video_items WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM video_items WHERE albumId = :albumId")
    suspend fun getCountByAlbum(albumId: Long): Int
}

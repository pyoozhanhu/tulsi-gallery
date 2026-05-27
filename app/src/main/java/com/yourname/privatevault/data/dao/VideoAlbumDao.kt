package com.yourname.privatevault.data.dao

import androidx.room.*
import com.yourname.privatevault.data.entity.VideoAlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoAlbumDao {
    @Query("SELECT * FROM video_albums WHERE folderId = :folderId ORDER BY sortOrder, createdAt")
    fun getAlbumsByFolder(folderId: Long): Flow<List<VideoAlbumEntity>>

    @Query("SELECT * FROM video_albums WHERE id = :id")
    suspend fun getAlbumById(id: Long): VideoAlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(album: VideoAlbumEntity): Long

    @Update
    suspend fun update(album: VideoAlbumEntity)

    @Delete
    suspend fun delete(album: VideoAlbumEntity)

    @Query("DELETE FROM video_albums WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE video_albums SET videoCount = :count WHERE id = :id")
    suspend fun updateVideoCount(id: Long, count: Int)

    @Query("UPDATE video_albums SET coverPath = :path WHERE id = :id")
    suspend fun updateCoverPath(id: Long, path: String?)

    @Query("SELECT MAX(sortOrder) FROM video_albums WHERE folderId = :folderId")
    suspend fun getMaxSortOrder(folderId: Long): Int?
}

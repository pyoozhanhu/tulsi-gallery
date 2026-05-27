package com.yourname.privatevault.data.dao

import androidx.room.*
import com.yourname.privatevault.data.entity.PhotoAlbumEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoAlbumDao {
    @Query("SELECT * FROM photo_albums WHERE folderId = :folderId ORDER BY sortOrder, createdAt")
    fun getAlbumsByFolder(folderId: Long): Flow<List<PhotoAlbumEntity>>

    @Query("SELECT * FROM photo_albums WHERE id = :id")
    suspend fun getAlbumById(id: Long): PhotoAlbumEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(album: PhotoAlbumEntity): Long

    @Update
    suspend fun update(album: PhotoAlbumEntity)

    @Delete
    suspend fun delete(album: PhotoAlbumEntity)

    @Query("DELETE FROM photo_albums WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE photo_albums SET photoCount = :count WHERE id = :id")
    suspend fun updatePhotoCount(id: Long, count: Int)

    @Query("UPDATE photo_albums SET coverPath = :path WHERE id = :id")
    suspend fun updateCoverPath(id: Long, path: String?)

    @Query("SELECT MAX(sortOrder) FROM photo_albums WHERE folderId = :folderId")
    suspend fun getMaxSortOrder(folderId: Long): Int?
}

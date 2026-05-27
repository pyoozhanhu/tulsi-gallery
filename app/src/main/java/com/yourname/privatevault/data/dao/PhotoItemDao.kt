package com.yourname.privatevault.data.dao

import androidx.room.*
import com.yourname.privatevault.data.entity.PhotoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoItemDao {
    @Query("SELECT * FROM photo_items WHERE albumId = :albumId ORDER BY addedAt")
    fun getItemsByAlbum(albumId: Long): Flow<List<PhotoItemEntity>>

    @Query("SELECT * FROM photo_items WHERE id = :id")
    suspend fun getItemById(id: Long): PhotoItemEntity?

    @Query("DELETE FROM photo_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PhotoItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PhotoItemEntity>)

    @Update
    suspend fun update(item: PhotoItemEntity)

    @Delete
    suspend fun delete(item: PhotoItemEntity)

    @Query("DELETE FROM photo_items WHERE albumId = :albumId")
    suspend fun deleteAllByAlbum(albumId: Long)

    @Query("DELETE FROM photo_items WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM photo_items WHERE albumId = :albumId")
    suspend fun getCountByAlbum(albumId: Long): Int
}

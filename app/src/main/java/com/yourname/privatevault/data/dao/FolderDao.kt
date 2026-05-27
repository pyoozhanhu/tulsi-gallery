package com.yourname.privatevault.data.dao

import androidx.room.*
import com.yourname.privatevault.data.entity.FolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Query("SELECT * FROM folders WHERE type = :type ORDER BY sortOrder, createdAt")
    fun getFoldersByType(type: String): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE id = :id")
    suspend fun getFolderById(id: Long): FolderEntity?

    @Query("SELECT * FROM folders WHERE id = :id")
    fun getFolderByIdFlow(id: Long): Flow<FolderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: FolderEntity): Long

    @Update
    suspend fun update(folder: FolderEntity)

    @Delete
    suspend fun delete(folder: FolderEntity)

    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT MAX(sortOrder) FROM folders WHERE type = :type")
    suspend fun getMaxSortOrder(type: String): Int?

    @Query("UPDATE folders SET sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateSortOrder(id: Long, sortOrder: Int)
}

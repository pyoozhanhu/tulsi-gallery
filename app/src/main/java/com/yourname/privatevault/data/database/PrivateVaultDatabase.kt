package com.yourname.privatevault.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yourname.privatevault.data.dao.*
import com.yourname.privatevault.data.entity.*

@Database(
    entities = [
        FolderEntity::class,
        MangaSeriesEntity::class,
        MangaPageEntity::class,
        VideoAlbumEntity::class,
        VideoItemEntity::class,
        PhotoAlbumEntity::class,
        PhotoItemEntity::class,
        ReadingProgressEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    autoMigrations = []
)
abstract class PrivateVaultDatabase : RoomDatabase() {
    abstract fun folderDao(): FolderDao
    abstract fun mangaSeriesDao(): MangaSeriesDao
    abstract fun mangaPageDao(): MangaPageDao
    abstract fun videoAlbumDao(): VideoAlbumDao
    abstract fun videoItemDao(): VideoItemDao
    abstract fun photoAlbumDao(): PhotoAlbumDao
    abstract fun photoItemDao(): PhotoItemDao
    abstract fun readingProgressDao(): ReadingProgressDao
    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: PrivateVaultDatabase? = null

        fun getDatabase(context: Context): PrivateVaultDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PrivateVaultDatabase::class.java,
                    "privatevault_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

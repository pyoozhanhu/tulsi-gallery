package com.yourname.privatevault.ui.navigation

import androidx.compose.runtime.Immutable

/**
 * 导航路由
 */
@Immutable
sealed class Screen {
    data object Manga : Screen()
    data object Video : Screen()
    data object Photo : Screen()
    
    data class MangaSeriesDetail(val seriesId: Long) : Screen()
    data class MangaReader(val seriesId: Long, val initialPage: Int) : Screen()
    
    data class VideoAlbumDetail(val albumId: Long) : Screen()
    data class VideoPlayer(val videoUri: String) : Screen()
    
    data class PhotoAlbumDetail(val albumId: Long) : Screen()
    data class PhotoViewer(val albumId: Long, val photoId: Long) : Screen()
    
    data object ExportManagement : Screen()
}

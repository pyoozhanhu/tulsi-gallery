package com.yourname.privatevault.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.yourname.privatevault.ui.manga.MangaReaderScreen
import com.yourname.privatevault.ui.manga.MangaSection
import com.yourname.privatevault.ui.manga.MangaSeriesDetail
import com.yourname.privatevault.ui.photo.PhotoAlbumDetail
import com.yourname.privatevault.ui.photo.PhotoSection
import com.yourname.privatevault.ui.photo.PhotoViewerScreen
import com.yourname.privatevault.ui.settings.ExportManagementScreen
import com.yourname.privatevault.ui.settings.SettingsScreen
import com.yourname.privatevault.ui.video.VideoAlbumDetail
import com.yourname.privatevault.ui.video.VideoSection
import com.yourname.privatevault.ui.video.VideoPlayerScreen

/**
 * 应用导航
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val showBottomBar = currentRoute in listOf(
        Screen.Manga::class.simpleName,
        Screen.Video::class.simpleName,
        Screen.Photo::class.simpleName
    )
    
    val bottomBarItems = listOf(
        Screen.Manga to "漫画" to Icons.Default.Book,
        Screen.Video to "视频" to Icons.Default.VideoLibrary,
        Screen.Photo to "图片" to Icons.Default.PhotoLibrary
    )
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
                    bottomBarItems.forEach { (screen, label, icon) ->
                        val isSelected = currentDestination == screen::class.simpleName
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen::class.simpleName!!) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    
                    // 设置按钮
                    val isSettingsSelected = currentDestination == "Settings"
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "设置") },
                        label = { Text("设置") },
                        selected = isSettingsSelected,
                        onClick = {
                            navController.navigate("Settings") {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Manga::class.simpleName!!,
                modifier = modifier
            ) {
                // ====== 漫画板块 ======
                composable(route = Screen.Manga::class.simpleName!!) {
                    MangaSection(
                        onNavigateToSeriesDetail = { seriesId ->
                            navController.navigate("${Screen.MangaSeriesDetail::class.simpleName}/$seriesId")
                        }
                    )
                }
                
                composable(
                    route = "${Screen.MangaSeriesDetail::class.simpleName}/{seriesId}",
                    arguments = listOf(navArgument("seriesId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val seriesId = backStackEntry.arguments?.getLong("seriesId") ?: return@composable
                    MangaSeriesDetail(
                        seriesId = seriesId,
                        onBack = { navController.popBackStack() },
                        onStartReading = { pages ->
                            navController.navigate("${Screen.MangaReader::class.simpleName}/$seriesId/0")
                        }
                    )
                }
                
                composable(
                    route = "${Screen.MangaReader::class.simpleName}/{seriesId}/{initialPage}",
                    arguments = listOf(
                        navArgument("seriesId") { type = NavType.LongType },
                        navArgument("initialPage") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val seriesId = backStackEntry.arguments?.getLong("seriesId") ?: return@composable
                    val initialPage = backStackEntry.arguments?.getInt("initialPage") ?: 0
                    MangaReaderScreen(
                        seriesId = seriesId,
                        pages = emptyList(), // TODO: 从 ViewModel 获取
                        initialPage = initialPage,
                        onProgressChange = { page, total ->
                            // TODO: 保存进度
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                
                // ====== 视频板块 ======
                composable(route = Screen.Video::class.simpleName!!) {
                    VideoSection(
                        onNavigateToAlbumDetail = { albumId ->
                            navController.navigate("${Screen.VideoAlbumDetail::class.simpleName}/$albumId")
                        }
                    )
                }
                
                composable(
                    route = "VideoAlbumDetail/{albumId}",
                    arguments = listOf(navArgument("albumId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val albumId = backStackEntry.arguments?.getLong("albumId") ?: return@composable
                    VideoAlbumDetail(
                        albumId = albumId,
                        onBack = { navController.popBackStack() },
                        onPlayVideo = { video ->
                            navController.navigate("VideoPlayer/${video.albumId}/${video.id}")
                        }
                    )
                }
                
                composable(
                    route = "VideoPlayer/{albumId}/{videoId}",
                    arguments = listOf(
                        navArgument("albumId") { type = NavType.LongType },
                        navArgument("videoId") { type = NavType.LongType }
                    )
                ) { backStackEntry ->
                    val albumId = backStackEntry.arguments?.getLong("albumId") ?: return@composable
                    val videoId = backStackEntry.arguments?.getLong("videoId") ?: return@composable
                    
                    val viewModel: VideoViewModel = viewModel()
                    val items by viewModel.getItemsByAlbum(albumId).collectAsState(initial = emptyList())
                    
                    val video = items.find { it.id == videoId } ?: return@composable
                    
                    VideoPlayerScreen(
                        video = video,
                        onBack = { navController.popBackStack() },
                        onDelete = { videoItem ->
                            viewModel.deleteItem(videoItem.id)
                            navController.popBackStack()
                        },
                        onRename = { videoItem, newName ->
                            viewModel.updateItem(videoItem.id, newName = newName)
                        }
                    )
                }
                
                // ====== 图片板块 ======
                composable(route = "Photo") {
                    PhotoSection(
                        onNavigateToAlbumDetail = { albumId ->
                            navController.navigate("PhotoAlbumDetail/$albumId")
                        }
                    )
                }
                
                composable(
                    route = "PhotoAlbumDetail/{albumId}",
                    arguments = listOf(navArgument("albumId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val albumId = backStackEntry.arguments?.getLong("albumId") ?: return@composable
                    PhotoAlbumDetail(
                        albumId = albumId,
                        onBack = { navController.popBackStack() },
                        onPhotoClick = { photo ->
                            navController.navigate("PhotoViewer/$albumId/${photo.id}")
                        }
                    )
                }
                
                composable(
                    route = "PhotoViewer/{albumId}/{photoId}",
                    arguments = listOf(
                        navArgument("albumId") { type = NavType.LongType },
                        navArgument("photoId") { type = NavType.LongType }
                    )
                ) { backStackEntry ->
                    val albumId = backStackEntry.arguments?.getLong("albumId") ?: return@composable
                    val photoId = backStackEntry.arguments?.getLong("photoId") ?: return@composable
                    
                    val viewModel: PhotoViewModel = viewModel()
                    val items by viewModel.getItemsByAlbum(albumId).collectAsState(initial = emptyList())
                    
                    val initialPageIndex = items.indexOfFirst { it.id == photoId }.coerceAtLeast(0)
                    
                    PhotoViewerScreen(
                        photos = items,
                        initialPage = initialPageIndex,
                        onBack = { navController.popBackStack() },
                        onDelete = { photo ->
                            viewModel.deleteItem(photo.id)
                            navController.popBackStack()
                        },
                        onRename = { photo, newName ->
                            viewModel.updateItem(photo.id, newName = newName)
                        }
                    )
                }
                
                // ====== 导出管理 ======
                composable(route = Screen.ExportManagement::class.simpleName!!) {
                    ExportManagementScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                
                // ====== 设置 ======
                composable(route = "Settings") {
                    SettingsScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateToExportManagement = {
                            navController.navigate(Screen.ExportManagement::class.simpleName!!)
                        }
                    )
                }
            }
        }
    }
}

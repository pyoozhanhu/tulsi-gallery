package com.yourname.privatevault.ui.photo

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIosNew
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.GlideImage
import com.yourname.privatevault.data.entity.PhotoItemEntity
import com.yourname.privatevault.ui.components.dialogs.ContextMenuDialog
import com.yourname.privatevault.ui.components.dialogs.DeleteConfirmationDialog
import com.yourname.privatevault.ui.components.dialogs.RenameDialog
import com.yourname.privatevault.util.ExportUtils
import java.io.File

/**
 * 全屏图片浏览器
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoViewerScreen(
    photos: List<PhotoItemEntity>,
    initialPage: Int = 0,
    onBack: () -> Unit,
    onDelete: ((PhotoItemEntity) -> Unit)? = null,
    onRename: ((PhotoItemEntity, String) -> Unit)? = null
) {
    var showToolbar by remember { mutableStateOf(true) }
    var showPhotoMenu by remember { mutableStateOf(false) }
    var photoToDelete by remember { mutableStateOf<PhotoItemEntity?>(null) }
    var photoToRename by remember { mutableStateOf<PhotoItemEntity?>(null) }
    var photoToExport by remember { mutableStateOf<PhotoItemEntity?>(null) }
    val context = LocalContext.current
    
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { photos.size })
    
    // 导出照片
    photoToExport?.let { photo ->
        LaunchedEffect(photo) {
            val sourceFile = File(photo.filePath)
            ExportUtils.exportFile(context, sourceFile)
            photoToExport = null
        }
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = showToolbar,
                enter = androidx.compose.animation.expandVertically(),
                exit = androidx.compose.animation.shrinkVertically()
            ) {
                TopAppBar(
                    title = { Text("${pagerState.currentPage + 1} / ${photos.size}") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showPhotoMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "更多"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black.copy(alpha = 0.8f)
                    )
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(visible = showToolbar) {
                BottomAppBar(
                    containerColor = Color.Black.copy(alpha = 0.8f)
                ) {
                    IconButton(
                        onClick = {
                            if (pagerState.currentPage > 0) {
                                launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        },
                        enabled = pagerState.currentPage > 0
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBackIosNew,
                            contentDescription = "上一张"
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            if (pagerState.currentPage < photos.size - 1) {
                                launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        enabled = pagerState.currentPage < photos.size - 1
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "下一张"
                        )
                    }
                }
            }
        },
        containerColor = Color.Black
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .detectTapGestures(
                    onTap = { showToolbar = !showToolbar }
                )
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(),
                pageSpacing = 0.dp
            ) { page ->
                val photo = photos[page]
                
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val photoFile = File(photo.filePath)
                    if (photoFile.exists()) {
                        GlideImage(
                            model = photoFile,
                            contentDescription = photo.fileName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(
                            text = "图片不存在",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
    
    // 照片菜单
    if (showPhotoMenu) {
        ContextMenuDialog(
            onDismiss = { showPhotoMenu = false },
            onRename = {
                photoToRename = photos[pagerState.currentPage]
                showPhotoMenu = false
            },
            onDelete = {
                photoToDelete = photos[pagerState.currentPage]
                showPhotoMenu = false
            },
            onExport = {
                photoToExport = photos[pagerState.currentPage]
                showPhotoMenu = false
            }
        )
    }
    
    // 删除确认
    photoToDelete?.let { photo ->
        DeleteConfirmationDialog(
            onDismiss = { photoToDelete = null },
            onConfirm = {
                photoToDelete?.let {
                    onDelete?.invoke(it)
                    photoToDelete = null
                }
                // 删除后返回
                if (photos.size == 1) {
                    onBack()
                }
            }
        )
    }
    
    // 重命名对话框
    photoToRename?.let { photo ->
        RenameDialog(
            currentName = photo.fileName,
            onDismiss = { photoToRename = null },
            onConfirm = { newName ->
                photoToRename?.let {
                    onRename?.invoke(it, newName)
                    photoToRename = null
                }
            }
        )
    }
}

package com.yourname.privatevault.ui.video

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.yourname.privatevault.data.entity.VideoItemEntity
import com.yourname.privatevault.ui.components.dialogs.ContextMenuDialog
import com.yourname.privatevault.ui.components.dialogs.DeleteConfirmationDialog
import com.yourname.privatevault.ui.components.dialogs.RenameDialog
import com.yourname.privatevault.util.ExportUtils

/**
 * 视频播放器界面（全屏）
 */
@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    video: VideoItemEntity,
    onBack: () -> Unit,
    onDelete: ((VideoItemEntity) -> Unit)? = null,
    onRename: ((VideoItemEntity, String) -> Unit)? = null
) {
    val context = LocalContext.current
    var showToolbar by remember { mutableStateOf(true) }
    var showVideoMenu by remember { mutableStateOf(false) }
    var videoToDelete by remember { mutableStateOf<VideoItemEntity?>(null) }
    var videoToRename by remember { mutableStateOf<VideoItemEntity?>(null) }
    var videoToExport by remember { mutableStateOf<VideoItemEntity?>(null) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(video.videoUri))
                prepare()
                playWhenReady = true
            }
    }

    DisposableEffect(
        key1 = exoPlayer
    ) {
        onDispose {
            exoPlayer.release()
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
                    title = { Text(video.fileName) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showVideoMenu = true }) {
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
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                factory = { exoPlayer.context ->
                    androidx.media3.ui.PlayerView(exoPlayer.context).apply {
                        player = exoPlayer
                        useController = true
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // 点击屏幕显示/隐藏工具栏
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 可以通过点击控制 toolbar 显示/隐藏
            }
        }
    }
    
    // 导出视频
    videoToExport?.let { video ->
        LaunchedEffect(video) {
            val sourceFile = File(video.filePath)
            ExportUtils.exportFile(context, sourceFile)
            videoToExport = null
        }
    }
    
    // 视频菜单
    if (showVideoMenu) {
        ContextMenuDialog(
            onDismiss = { showVideoMenu = false },
            onRename = {
                videoToRename = video
                showVideoMenu = false
            },
            onDelete = {
                videoToDelete = video
                showVideoMenu = false
            },
            onExport = {
                videoToExport = video
                showVideoMenu = false
            }
        )
    }
    
    // 删除确认
    videoToDelete?.let { videoItem ->
        DeleteConfirmationDialog(
            onDismiss = { videoToDelete = null },
            onConfirm = {
                videoToDelete?.let {
                    onDelete?.invoke(it)
                    videoToDelete = null
                }
                onBack()
            }
        )
    }
    
    // 重命名对话框
    videoToRename?.let { videoItem ->
        RenameDialog(
            currentName = videoItem.fileName,
            onDismiss = { videoToRename = null },
            onConfirm = { newName ->
                videoToRename?.let {
                    onRename?.invoke(it, newName)
                    videoToRename = null
                }
            }
        )
    }
}
    }

    DisposableEffect(
        key1 = exoPlayer
    ) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { exoPlayer.context ->
                androidx.media3.ui.PlayerView(exoPlayer.context).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 可选的返回按钮或手势
    }
}

package com.yourname.privatevault.ui.photo

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.GlideImage
import com.yourname.privatevault.data.entity.FolderEntity
import com.yourname.privatevault.data.entity.PhotoAlbumEntity
import com.yourname.privatevault.ui.components.CollapsibleFolder
import com.yourname.privatevault.ui.components.EmptyState
import com.yourname.privatevault.ui.components.dialogs.ContextMenuDialog
import com.yourname.privatevault.ui.components.dialogs.DeleteConfirmationDialog
import com.yourname.privatevault.ui.components.dialogs.RenameDialog
import java.io.File

/**
 * 图片板块主页 - 折叠框列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoSection(
    onNavigateToAlbumDetail: (Long) -> Unit,
    viewModel: PhotoViewModel = viewModel()
) {
    val folders by viewModel.getFolders().collectAsState(initial = emptyList())
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var folderToRename by remember { mutableStateOf<FolderEntity?>(null) }
    var folderToDelete by remember { mutableStateOf<FolderEntity?>(null) }
    var showRenameDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("图片") },
                actions = {
                    IconButton(onClick = { showCreateFolderDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "创建折叠框")
                    }
                }
            )
        }
    ) { padding ->
        if (folders.isEmpty()) {
            EmptyState(
                message = "暂无图片分类",
                actionLabel = "创建第一个分类",
                onActionClick = { showCreateFolderDialog = true }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(folders.size) { index ->
                    val folder = folders[index]
                    var isExpanded by remember { mutableStateOf(false) }
                    val albums by viewModel.getAlbumsByFolder(folder.id)
                        .collectAsState(initial = emptyList())

                    CollapsibleFolder(
                        folderName = folder.name,
                        isExpanded = isExpanded,
                        onToggle = { isExpanded = !isExpanded },
                        onCreateItem = { viewModel.createAlbum(folder.id, "新相册") },
                        createLabel = "创建相册",
                        onRename = {
                            folderToRename = folder
                            showRenameDialog = true
                        },
                        onDelete = { folderToDelete = folder },
                        items = albums.map { it as Any },
                        onItemClick = { albumIndex ->
                            if (albums.isNotEmpty()) {
                                val album = albums[albumIndex]
                                onNavigateToAlbumDetail(album.id)
                            }
                        },
                        onItemLongClick = { albumIndex ->
                            // TODO: 显示长按菜单
                        },
                        itemContent = { albumIndex ->
                            if (albums.isNotEmpty()) {
                                PhotoAlbumCard(albums.getOrNull(albumIndex))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoAlbumCard(album: PhotoAlbumEntity?) {
    if (album == null) return
    
    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 封面图片
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (!album.coverPath.isNullOrEmpty()) {
                    val coverFile = File(album.coverPath)
                    if (coverFile.exists()) {
                        GlideImage(
                            model = coverFile,
                            contentDescription = album.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("📷", fontSize = 32.sp)
                    }
                } else {
                    Text("📷", fontSize = 32.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = album.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
            
            Text(
                text = "${album.photoCount}张照片",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

package com.yourname.privatevault.ui.manga

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.GlideImage
import com.yourname.privatevault.data.entity.MangaSeriesEntity
import com.yourname.privatevault.ui.components.CollapsibleFolder
import com.yourname.privatevault.ui.components.EmptyState
import com.yourname.privatevault.ui.components.dialogs.ContextMenuDialog
import com.yourname.privatevault.ui.components.dialogs.DeleteConfirmationDialog
import com.yourname.privatevault.ui.components.dialogs.RenameDialog
import java.io.File

/**
 * 漫画板块主页 - 折叠框列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaSection(
    onNavigateToSeriesDetail: (Long) -> Unit,
    viewModel: MangaViewModel = viewModel()
) {
    val folders by viewModel.getFoldersByType().collectAsState(initial = emptyList())
    var selectedFolderId by remember { mutableStateOf<Long?>(null) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var folderToRename by remember { mutableStateOf<FolderEntity?>(null) }
    var folderToDelete by remember { mutableStateOf<FolderEntity?>(null) }
    
    // 漫画集相关状态
    var seriesToShowMenu by remember { mutableStateOf<MangaSeriesEntity?>(null) }
    var seriesToDelete by remember { mutableStateOf<MangaSeriesEntity?>(null) }
    var showSeriesRenameDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("漫画") },
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
                message = "暂无漫画分类",
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
                    val seriesList by viewModel.getSeriesByFolder(folder.id)
                        .collectAsState(initial = emptyList())

                    CollapsibleFolder(
                        folderName = folder.name,
                        isExpanded = isExpanded,
                        onToggle = { isExpanded = !isExpanded },
                        onCreateItem = { viewModel.createSeries(folder.id, "新漫画集") },
                        createLabel = "创建漫画集",
                        onRename = {
                            folderToRename = folder
                            showRenameDialog = true
                        },
                        onDelete = { folderToDelete = folder },
                        items = seriesList.map { it as Any },
                        onItemClick = { seriesIndex ->
                            if (seriesList.isNotEmpty()) {
                                val series = seriesList[seriesIndex]
                                onNavigateToSeriesDetail(series.id)
                            }
                        },
                        onItemLongClick = { seriesIndex ->
                            if (seriesList.isNotEmpty()) {
                                seriesToShowMenu = seriesList[seriesIndex]
                            }
                        },
                        itemContent = { seriesIndex ->
                            if (seriesList.isNotEmpty()) {
                                MangaSeriesCard(seriesList.getOrNull(seriesIndex))
                            }
                        }
                    )
                }
            }
        }
    }
    
    // 漫画集长按菜单
    seriesToShowMenu?.let { series ->
        Box {
            // 这里需要一个更好的定位方式，暂时简单处理
            ContextMenuDialog(
                onDismiss = { seriesToShowMenu = null },
                onRename = {
                    seriesToShowMenu = series
                    showSeriesRenameDialog = true
                },
                onDelete = { seriesToDelete = series }
            )
        }
    }
    
    // 删除确认
    seriesToDelete?.let { series ->
        DeleteConfirmationDialog(
            onDismiss = { seriesToDelete = null },
            onConfirm = {
                viewModel.deleteSeries(series)
                seriesToDelete = null
            }
        )
    }
    
    // 重命名对话框
    if (showSeriesRenameDialog && seriesToShowMenu != null) {
        RenameDialog(
            currentName = seriesToShowMenu!!.name,
            onDismiss = { showSeriesRenameDialog = false },
            onConfirm = { newName ->
                viewModel.updateSeries(seriesToShowMenu!!.copy(name = newName))
                showSeriesRenameDialog = false
                seriesToShowMenu = null
            }
        )
    }
}

@Composable
fun MangaSeriesCard(series: MangaSeriesEntity?) {
    if (series == null) return
    
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
                if (!series.coverPath.isNullOrEmpty()) {
                    val coverFile = File(series.coverPath)
                    if (coverFile.exists()) {
                        GlideImage(
                            model = coverFile,
                            contentDescription = series.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("📚", fontSize = 32.sp)
                    }
                } else {
                    Text("📚", fontSize = 32.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 标题
            Text(
                text = series.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
            
            // 页数信息
            Text(
                text = "${series.pageCount}页",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CreateFolderDialog(
    showDialog: () -> Unit,
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建漫画分类") },
        text = { Text("请输入分类名称") },
        confirmButton = {
            TextButton(onClick = { onCreate("新分类") }) {
                Text("创建")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

package com.yourname.privatevault.ui.photo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Export
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.yourname.privatevault.data.entity.PhotoItemEntity
import com.yourname.privatevault.ui.components.dialogs.ContextMenuDialog
import com.yourname.privatevault.ui.components.dialogs.DeleteConfirmationDialog
import com.yourname.privatevault.ui.components.dialogs.RenameDialog
import com.yourname.privatevault.util.ExportUtils
import com.yourname.privatevault.util.FileStorageManager
import java.io.File

@Composable
fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var fileName: String? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst()) {
            fileName = cursor.getString(nameIndex)
        }
    }
    return fileName ?: uri.lastPathSegment
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoAlbumDetail(
    albumId: Long,
    onBack: () -> Unit,
    onPhotoClick: (PhotoItemEntity) -> Unit,
    viewModel: PhotoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    var showPhotoMenu by remember { mutableStateOf(false) }
    var photoToDelete by remember { mutableStateOf<PhotoItemEntity?>(null) }
    var photoToRename by remember { mutableStateOf<PhotoItemEntity?>(null) }
    var photoToExport by remember { mutableStateOf<PhotoItemEntity?>(null) }
    var selectedItems by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var showBulkMenu by remember { mutableStateOf(false) }
    
    val items by viewModel.getItemsByAlbum(albumId).collectAsState(initial = emptyList())
    val filteredItems = remember(items) { items.filterNot { it.isDeleted } }
    
    val isSelectionMode = selectedItems.isNotEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (isSelectionMode) {
                        Text("已选择 ${selectedItems.size} 项")
                    } else {
                        Text("相册详情")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSelectionMode) {
                            selectedItems = emptySet()
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    if (isSelectionMode) {
                        IconButton(onClick = {
                            showBulkMenu = true
                        }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "批量操作"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 添加按钮区域
            if (!isSelectionMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    val imagePicker = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.PickMultipleVisualMedia()
                    ) { uris: List<Uri> ->
                        if (uris.isNotEmpty()) {
                            uris.forEach { uri ->
                                val fileName = getFileNameFromUri(context, uri) ?: "photo_${System.currentTimeMillis()}.jpg"
                                val filePath = FileStorageManager.saveFileFromUri(
                                    context = context,
                                    uri = uri,
                                    subDir = "photo/$albumId",
                                    fileName = fileName
                                )
                                
                                viewModel.insertItem(
                                    albumId = albumId,
                                    filePath = filePath,
                                    fileName = fileName
                                )
                            }
                        }
                    }

                    Button(onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.READ_MEDIA_IMAGES
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    }) {
                        Text("＋ 添加照片")
                    }
                }
            }

            // 照片网格
            if (filteredItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无照片，请点击\"添加照片\"")
                }
            } else {
                LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredItems) { item ->
                        PhotoGridItem(
                            item = item,
                            isSelected = selectedItems.contains(item.id),
                            onClick = {
                                if (isSelectionMode) {
                                    selectedItems = if (selectedItems.contains(item.id)) {
                                        selectedItems - item.id
                                    } else {
                                        selectedItems + item.id
                                    }
                                } else {
                                    onPhotoClick(item)
                                }
                            },
                            onLongClick = {
                                selectedItems = setOf(item.id)
                                showPhotoMenu = true
                            }
                        )
                    }
                }
            }
        }
    }
    
    // 单个照片菜单
    if (showPhotoMenu) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ContextMenuDialog(
                onDismiss = { 
                    showPhotoMenu = false
                    if (selectedItems.isEmpty()) {
                        // 如果不是通过选择模式触发的，清空选择
                    }
                },
                onRename = {
                    photoToRename = filteredItems.find { it.id in selectedItems }
                    showPhotoMenu = false
                },
                onDelete = {
                    photoToDelete = filteredItems.find { it.id in selectedItems }
                    showPhotoMenu = false
                },
                onExport = {
                    photoToExport = filteredItems.find { it.id in selectedItems }
                    showPhotoMenu = false
                }
            )
        }
    }
    
    // 批量操作菜单
    if (showBulkMenu) {
        AlertDialog(
            onDismissRequest = { showBulkMenu = false },
            title = { Text("批量操作") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            // 批量删除
                            photoToDelete = filteredItems.find { it.id == selectedItems.firstOrNull() }
                            showBulkMenu = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("删除所选 (${selectedItems.size})")
                    }
                    
                    TextButton(
                        onClick = {
                            // 批量导出
                            selectedItems.forEach { itemId ->
                                filteredItems.find { it.id == itemId }?.let { item ->
                                    val sourceFile = File(item.filePath)
                                    ExportUtils.exportFile(context, sourceFile)
                                }
                            }
                            Toast.makeText(context, "已导出 ${selectedItems.size} 个文件到 files/exports/", Toast.LENGTH_LONG).show()
                            selectedItems = emptySet()
                            showBulkMenu = false
                        }
                    ) {
                        Icon(Icons.Default.Export, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("导出所选 (${selectedItems.size})")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showBulkMenu = false }) {
                    Text("取消")
                }
            }
        )
    }
    
    // 删除确认
    photoToDelete?.let { photo ->
        DeleteConfirmationDialog(
            onDismiss = { photoToDelete = null },
            onConfirm = {
                if (selectedItems.size > 1) {
                    // 批量删除
                    selectedItems.forEach { itemId ->
                        viewModel.deleteItem(itemId)
                    }
                    selectedItems = emptySet()
                } else {
                    viewModel.deleteItem(photo.id)
                }
                photoToDelete = null
            }
        )
    }
    
    // 重命名对话框
    photoToRename?.let { photo ->
        RenameDialog(
            currentName = photo.fileName,
            onDismiss = { photoToRename = null },
            onConfirm = { newName ->
                viewModel.updateItem(photo.id, newName = newName)
                photoToRename = null
            }
        )
    }
    
    // 导出照片
    photoToExport?.let { photo ->
        LaunchedEffect(photo) {
            val sourceFile = File(photo.filePath)
            val exportedPath = ExportUtils.exportFile(context, sourceFile)
            if (exportedPath != null) {
                Toast.makeText(context, "已导出到 files/exports/${File(exportedPath).name}", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "导出失败", Toast.LENGTH_SHORT).show()
            }
            photoToExport = null
            selectedItems = emptySet()
        }
    }
}

@Composable
fun PhotoGridItem(
    item: PhotoItemEntity?,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val context = LocalContext.current
    val imageLoader = remember { ImageLoader(context) }
    
    Card(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .then(
                if (isSelected) {
                    Modifier.border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            ),
        onClick = onClick,
        onLongClick = onLongClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (item != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(File(item.filePath))
                        .crossfade(true)
                        .build(),
                    imageLoader = imageLoader,
                    contentDescription = item.fileName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                if (isSelected) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(24.dp)
                    )
                }
                
                if (item.fileName.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(4.dp)
                    ) {
                        Text(
                            text = item.fileName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            maxLines = 2
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("加载失败")
                }
            }
        }
    }
}

package com.yourname.privatevault.ui.video

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourname.privatevault.data.entity.VideoItemEntity
import com.yourname.privatevault.util.FileStorageManager
import java.io.File

/**
 * 从 URI 获取文件名
 */
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

/**
 * 视频集内部页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoAlbumDetail(
    albumId: Long,
    onBack: () -> Unit,
    onPlayVideo: (VideoItemEntity) -> Unit,
    viewModel: VideoViewModel = viewModel()
) {
    val items by viewModel.getItemsByAlbum(albumId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("视频详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
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
            // ====== 导入按钮 ======
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                val context = LocalContext.current
                val videoPicker = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickMultipleVisualMedia()
                ) { uris: List<Uri> ->
                    if (uris.isNotEmpty()) {
                        uris.forEach { uri ->
                            val fileName = getFileNameFromUri(context, uri) ?: "video_${System.currentTimeMillis()}.mp4"
                            val filePath = FileStorageManager.saveFileFromUri(
                                context = context,
                                uri = uri,
                                subDir = "video/$albumId",
                                fileName = fileName
                            )
                            
                            viewModel.insertItem(
                                albumId = albumId,
                                filePath = filePath,
                                fileName = fileName,
                                duration = 0 // TODO: 获取视频时长
                            )
                        }
                    }
                }

                Button(onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_MEDIA_VIDEO
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        videoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                    }
                }) {
                    Text("＋ 导入视频")
                }
            }

            // ====== 视频网格 ======
            if (items.isEmpty()) {
                EmptyState(
                    message = "暂无视频，请点击\"导入视频"添加"
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items.size) { index ->
                        val item = items[index]
                        VideoItemCard(item) {
                            onPlayVideo(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoItemCard(
    item: VideoItemEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(16f / 9f),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎬", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.fileName,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2
                )
            }
        }
    }
}

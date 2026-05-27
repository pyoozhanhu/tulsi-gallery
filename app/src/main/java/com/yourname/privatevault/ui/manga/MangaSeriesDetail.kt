package com.yourname.privatevault.ui.manga

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourname.privatevault.data.entity.MangaPageEntity
import com.yourname.privatevault.util.FileNameSorter
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
 * 漫画集内部页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaSeriesDetail(
    seriesId: Long,
    onBack: () -> Unit,
    onStartReading: (List<MangaPageEntity>) -> Unit,
    viewModel: MangaViewModel = viewModel()
) {
    val series by viewModel.getSeriesById(seriesId).collectAsState(initial = null)
    val pages by viewModel.getPagesBySeries(seriesId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(series?.name ?: "漫画详情") },
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
            // ====== 顶部按钮行 ======
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val context = LocalContext.current
                val imagePicker = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickMultipleVisualMedia()
                ) { uris: List<Uri> ->
                    if (uris.isNotEmpty()) {
                        // 保存文件并导入数据库
                        uris.forEach { uri ->
                            val fileName = getFileNameFromUri(context, uri) ?: "img_${System.currentTimeMillis()}.jpg"
                            val pageNumber = FileNameSorter.extractNumber(fileName)
                            val filePath = FileStorageManager.saveFileFromUri(
                                context = context,
                                uri = uri,
                                subDir = "manga/$seriesId",
                                fileName = fileName
                            )
                            
                            // 创建页面实体
                            val page = MangaPageEntity(
                                seriesId = seriesId,
                                filePath = filePath,
                                pageNumber = pageNumber,
                                fileName = fileName
                            )
                            
                            viewModel.insertPages(listOf(page))
                            
                            // 更新漫画集信息
                            series?.let { s ->
                                viewModel.updateSeries(s.copy(
                                    pageCount = s.pageCount + 1,
                                    coverPath = if (s.pageCount == 0) filePath else s.coverPath
                                ))
                            }
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
                    Icon(Icons.Default.Add, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("导入图片")
                }

                Button(onClick = {
                    if (pages.isNotEmpty()) {
                        onStartReading(pages)
                    }
                }, enabled = pages.isNotEmpty()) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("开始阅读")
                }
            }

            // ====== 页面网格（4 列） ======
            if (pages.isEmpty()) {
                EmptyState(
                    message = "暂无内容，请点击\"导入图片\"添加"
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pages.size) { index ->
                        val page = pages[index]
                        
                        MangaPageCard(page) {
                            // TODO: 进入多选模式
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MangaPageCard(
    page: MangaPageEntity,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            // TODO: 加载页面缩略图
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (page.pageNumber > 0) {
                    Text(
                        text = "Page ${page.pageNumber}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Text(
                    text = page.fileName,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2
                )
            }
        }
    }
}

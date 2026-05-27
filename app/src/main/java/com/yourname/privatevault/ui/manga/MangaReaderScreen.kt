package com.yourname.privatevault.ui.manga

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIosNew
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.yourname.privatevault.data.entity.MangaPageEntity
import java.io.File

/**
 * 漫画阅读器 - 上下滑动翻页
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun MangaReaderScreen(
    seriesId: Long,
    pages: List<MangaPageEntity>,
    initialPage: Int = 0,
    onBack: () -> Unit,
    onPageUpdate: ((MangaPageEntity) -> Unit)? = null
) {
    var showToolbar by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val viewModel: MangaViewModel = viewModel()
    val savedStateHandle = (LocalContext.current as? androidx.activity.ComponentActivity)?.savedStateHandle
    
    val listState = rememberLazyListState(firstVisibleItemIndex = initialPage)
    val scope = rememberCoroutineScope()
    
    // 自动保存阅读进度
    LaunchedEffect(listState.firstVisibleItemIndex) {
        val currentIndex = listState.firstVisibleItemIndex
        if (currentIndex >= 0 && currentIndex < pages.size && pages.isNotEmpty()) {
            // 更新当前阅读进度
            viewModel.updateReadingProgress(
                seriesId = seriesId,
                currentPage = currentIndex + 1,
                totalPages = pages.size
            )
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
                    title = { Text("${listState.firstVisibleItemIndex + 1} / ${pages.size}") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    )
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(visible = showToolbar) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ) {
                    IconButton(
                        onClick = {
                            if (currentPage > 0) {
                                scrollState.animateScrollToItem(currentPage - 1)
                            }
                        },
                        enabled = currentPage > 0
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBackIosNew,
                            contentDescription = "上一页"
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            if (currentPage < pages.size - 1) {
                                scrollState.animateScrollToItem(currentPage + 1)
                            }
                        },
                        enabled = currentPage < pages.size - 1
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "下一页"
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
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .detectTapGestures {
                        showToolbar = !showToolbar
                    },
                state = scrollState,
                contentPadding = PaddingValues(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(pages.size) { index ->
                    val page = pages[index]
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.67f) // 常见漫画比例 2:3
                    ) {
                        GlideImage(
                            model = ImageRequest.Builder(context)
                                .data(File(page.filePath))
                                .crossfade(true)
                                .build(),
                            contentDescription = "Page ${page.pageNumber}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillWidth,
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(32.dp),
                                        color = Color.White
                                    )
                                }
                            },
                            failure = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "加载失败",
                                        color = Color.White
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

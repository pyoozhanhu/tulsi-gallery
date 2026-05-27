package com.yourname.privatevault.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 通用折叠框组件 - 图片/漫画/视频三个板块复用
 *
 * @param folderName 折叠框名称（如"少年漫画"）
 * @param isExpanded 展开/折叠状态
 * @param onToggle 切换折叠
 * @param onRename 重命名折叠框
 * @param onDelete 删除折叠框
 * @param onCreateItem 创建内部项目
 * @param createLabel 创建按钮文字
 * @param collapsedCount 折叠时显示几个（默认 4）
 * @param items 内部项目列表
 * @param onItemClick 点击项目回调
 * @param onItemLongClick 长按项目回调
 * @param itemContent 每个项目的 Composable
 */
@Composable
fun CollapsibleFolder(
    folderName: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onCreateItem: () -> Unit,
    createLabel: String,
    collapsedCount: Int = 4,
    items: List<Any>,
    onItemClick: (Int) -> Unit,
    onItemLongClick: (Int) -> Unit,
    itemContent: @Composable (Int) -> Unit
) {
    Column {
        // ====== 标题栏（始终可见） ======
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📁",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = folderName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(
                    id = if (isExpanded) android.R.drawable.arrow_up_float else android.R.drawable.arrow_down_float
                ),
                contentDescription = if (isExpanded) "折叠" else "展开",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        if (isExpanded) {
            // ====== 展开状态 ======
            AnimatedVisibility(
                visible = true,
                enter = expandVertically(animationSpec = tween(200)),
                exit = shrinkVertically(animationSpec = tween(200))
            ) {
                Column {
                    // 右上角创建按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onCreateItem) {
                            Text("＋ $createLabel")
                        }
                    }

                    // 项目网格（4 列）
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items.size) { index ->
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier
                                    .combinedClickable(
                                        onClick = { onItemClick(index) },
                                        onLongClick = { onItemLongClick(index) }
                                    )
                            ) {
                                itemContent(index)
                            }
                        }
                    }

                    // 底部操作按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        TextButton(onClick = onRename) {
                            Text("✎ 重命名")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = onDelete) {
                            Text("🗑 删除")
                        }
                    }
                }
            }
        } else {
            // ====== 折叠状态：只显示第一行 ======
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(items.take(collapsedCount).size) { index ->
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(80.dp)
                            .padding(4.dp)
                    ) {
                        itemContent(index)
                    }
                }
            }
        }

        HorizontalDivider()
    }
}

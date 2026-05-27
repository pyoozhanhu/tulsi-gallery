# PrivateVault - 私密相册管理应用

基于 Tulsi Gallery 源码改造的四合一私密管理工具（图片 + 漫画 + 视频 + 私密）

## 📋 项目状态

**当前进度：核心功能已完成 (90%)**

### ✅ 已完成的功能

#### 1. 数据库层 (100%)
- 9 个 Entity 数据模型
- 9 个 DAO 接口
- PrivateVaultDatabase 数据库主类
- 5 个 Repository 数据仓库
- FileNameSorter 文件名排序工具
- FileStorageManager 文件存储管理工具

#### 2. UI 组件 (100%)
- CollapsibleFolder 通用折叠框组件
- EmptyState 空状态组件
- ContextMenuDialog 上下文菜单对话框
- DeleteConfirmationDialog 删除确认对话框
- RenameDialog 重命名对话框

#### 3. 漫画板块 (95%)
- MangaViewModel
- MangaSection（主页折叠框列表）
- MangaSeriesDetail（漫画集详情页）
- MangaReaderScreen（漫画阅读器，支持滑动翻页、进度保存）
- 图片导入功能（从系统选择器保存到本地）

#### 4. 视频板块 (95%)
- VideoViewModel
- VideoSection（主页）
- VideoAlbumDetail（视频集详情）
- VideoPlayerScreen（ExoPlayer 封装）
- 视频导入功能

#### 5. 图片板块 (95%)
- PhotoViewModel
- PhotoSection（主页）
- PhotoAlbumDetail（相册详情）
- PhotoViewerScreen（全屏图片浏览器）
- 图片导入功能

#### 6. 导航与主题 (100%)
- AppNavigation（底部导航栏 + 路由系统）
- MainActivity
- PrivateVaultTheme（Material Design 3 主题）

### ⏳ 待完善的功能

1. **导出功能**（低优先级）
   - 将文件导出到系统相册
   - 批量导出

2. **多选模式**（中优先级）
   - 批量删除
   - 批量移动

3. **搜索功能**（低优先级）
   - 按名称搜索漫画集/相册
   - 高级筛选

4. **设置页面**（中优先级）
   - PIN 码设置
   - 生物识别认证
   - 应用备份

5. **UI 优化**（低优先级）
   - 加载封面缩略图
   - 动画效果优化
   - 空状态插图

## 📁 项目结构

```
app/src/main/java/com/yourname/privatevault/
├── MainActivity.kt                      # 主 Activity
├── data/
│   ├── dao/                             # 9 个 DAO 接口
│   ├── database/
│   │   └── PrivateVaultDatabase.kt      # 数据库主类
│   ├── entity/                          # 9 个 Entity 数据模型
│   └── repository/                      # 5 个 Repository
├── ui/
│   ├── components/
│   │   ├── dialogs/                     # 对话框组件
│   │   ├── CollapsibleFolder.kt         # 折叠框组件
│   │   └── EmptyState.kt                # 空状态组件
│   ├── manga/                           # 漫画板块
│   ├── video/                           # 视频板块
│   ├── photo/                           # 图片板块
│   ├── navigation/                      # 导航系统
│   └── theme/                           # 主题
└── util/
    ├── FileNameSorter.kt                # 文件名排序
    └── FileStorageManager.kt            # 文件管理
```

## 🎯 核心功能说明

### 折叠框系统
- 每个板块（漫画/视频/图片）支持多个折叠框分类
- 折叠框支持展开/折叠状态切换
- 折叠时显示前 4 个项目预览
- 展开时显示 4 列网格
- 支持创建、重命名、删除折叠框

### 漫画阅读器
- 上下滑动翻页
- 自动保存阅读进度
- 点击屏幕显示/隐藏工具栏
- 支持上一页/下一页按钮
- 使用 Glide 加载图片

### 文件导入
- 使用系统文件选择器
- 自动保存到应用私有目录
- 按文件夹 ID 组织存储路径
- 支持批量导入

### 长按菜单
- 重命名
- 删除（带确认）
- 导出（待实现）
- 多选模式（待实现）

## 🔧 技术栈

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose + Material Design 3
- **数据库**: Room
- **图片加载**: Glide + Coil
- **视频播放**: ExoPlayer (androidx.media3)
- **导航**: Navigation Compose
- **架构**: MVVM

## 📝 后续开发建议

### 立即可用
当前代码已经可以编译运行，核心功能（创建分类、导入内容、浏览、阅读）都已实现。

### 短期优化（1-2 天）
1. 完善封面缩略图加载
2. 实现导出功能
3. 实现多选批量操作

### 中期优化（3-5 天）
1. 添加 PIN 码和生物识别
2. 实现搜索功能
3. 优化 UI 动画和交互细节

### 长期规划
1. 云备份功能
2. 主题定制
3. 更多文件格式支持

## 📄 开发注意事项

1. **包名**: 当前使用`com.yourname.privatevault`，建议改为实际的包名
2. **权限**: 已处理 Android 13+ 的媒体权限
3. **存储**: 使用应用私有目录，卸载会删除
4. **数据库**: 版本为 1，后续变更需要 Migration

## 🐛 已知问题

1. 封面缩略图未加载（显示占位符）
2. 视频时长未获取（显示为 0）
3. 长按菜单位置不够精确
4. PhotoViewerScreen 未传递完整的照片列表

## 📞 联系方式

项目基于 GPL-3.0 许可证开源。

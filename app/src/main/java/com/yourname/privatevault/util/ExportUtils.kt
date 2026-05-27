package com.yourname.privatevault.util

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * 文件导出工具
 * 将文件导出到应用私有目录内的可共享区域
 * 该区域有 .nomedia 保护，系统相册看不到，但用户可以通过文件管理器访问
 */
object ExportUtils {
    
    /**
     * 导出的目标目录
     * 位于应用私有目录内，但有独立的 .nomedia 文件
     */
    const val EXPORT_SUBDIR = "exports"
    
    /**
     * 导出单个文件到导出目录
     * @param context 上下文
     * @param sourceFile 源文件（应用私有目录内的文件）
     * @param exportFileName 导出后的文件名（可选，默认使用原文件名）
     * @return 导出后的文件路径，失败返回 null
     */
    fun exportFile(context: Context, sourceFile: File, exportFileName: String? = null): String? {
        if (!sourceFile.exists()) {
            return null
        }
        
        return try {
            // 创建导出目录
            val exportDir = File(context.filesDir, EXPORT_SUBDIR).apply {
                if (!exists()) {
                    mkdirs()
                    // 创建 .nomedia 文件，防止系统相册扫描
                    File(this, ".nomedia").createNewFile()
                }
            }
            
            val fileName = exportFileName ?: sourceFile.name
            val targetFile = File(exportDir, fileName)
            
            // 复制文件
            FileInputStream(sourceFile).use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            targetFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
            }
            
            targetFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 批量导出文件
     * @param context 上下文
     * @param sourceFiles 源文件列表
     * @return 导出结果（成功数量/失败数量）
     */
    fun exportFiles(context: Context, sourceFiles: List<File>): ExportResult {
        var successCount = 0
        var failCount = 0
        val exportedPaths = mutableListOf<String>()
        
        sourceFiles.forEach { sourceFile ->
            if (!sourceFile.exists()) {
                failCount++
                return@forEach
            }
            
            val exportedPath = exportFile(context, sourceFile)
            if (exportedPath != null) {
                successCount++
                exportedPaths.add(exportedPath)
            } else {
                failCount++
            }
        }
        
        return ExportResult(successCount, failCount, exportedPaths)
    }
    
    /**
     * 获取导出目录
     */
    fun getExportDirectory(context: Context): File {
        return File(context.filesDir, EXPORT_SUBDIR)
    }
    
    /**
     * 获取所有已导出的文件
     */
    fun getExportedFiles(context: Context): List<File> {
        val exportDir = getExportDirectory(context)
        if (!exportDir.exists()) {
            return emptyList()
        }
        
        return exportDir.listFiles { file ->
            file.isFile && file.name != ".nomedia"
        }?.toList() ?: emptyList()
    }
    
    /**
     * 清除导出目录中的所有文件
     */
    fun clearExportDirectory(context: Context): Boolean {
        val exportDir = getExportDirectory(context)
        if (!exportDir.exists()) {
            return true
        }
        
        exportDir.listFiles()?.forEach { file ->
            if (file.name != ".nomedia") {
                file.delete()
            }
        }
        
        return true
    }
    
    /**
     * 删除单个导出文件
     */
    fun deleteExportedFile(context: Context, fileName: String): Boolean {
        val exportDir = getExportDirectory(context)
        val file = File(exportDir, fileName)
        return file.exists() && file.delete()
    }
    
    /**
     * 导出结果
     */
    data class ExportResult(
        val successCount: Int,
        val failCount: Int,
        val exportedPaths: List<String>
    )
}

package com.yourname.privatevault.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * 文件管理工具类
 * 负责将URI 转换为本地文件并存储
 */
object FileStorageManager {

    /**
     * 存储图片文件到应用私有目录
     * @param context 上下文
     * @param uri 源文件 URI
     * @param subDir 子目录（manga/video/photo）
     * @param fileName 文件名
     * @return 保存后的文件路径
     */
    fun saveFileFromUri(context: Context, uri: Uri, subDir: String, fileName: String): String {
        val directory = File(context.filesDir, subDir).apply {
            if (!exists()) {
                mkdirs()
                // 创建 .nomedia 文件，防止系统相册扫描
                File(this, ".nomedia").createNewFile()
            }
        }
        
        val outputFile = File(directory, fileName)
        
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(outputFile).use { outputStream ->
                copyStream(inputStream, outputStream)
            }
        }
        
        return outputFile.absolutePath
    }

    /**
     * 批量存储文件
     * @param context 上下文
     * @param uriList URI 列表
     * @param subDir 子目录
     * @return 保存后的文件路径列表
     */
    fun saveFilesFromUris(context: Context, uriList: List<Uri>, subDir: String): List<Pair<String, String>> {
        return uriList.map { uri ->
            val fileName = getFileNameFromUri(context, uri) ?: "temp_${System.currentTimeMillis()}"
            val filePath = saveFileFromUri(context, uri, subDir, fileName)
            fileName to filePath
        }
    }

    /**
     * 从 URI 获取文件名
     */
    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        
        // 尝试从 display name 获取
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }
        
        // 如果失败，尝试从 path 获取
        if (fileName == null) {
            fileName = uri.lastPathSegment
        }
        
        return fileName
    }

    /**
     * 删除文件
     */
    fun deleteFile(filePath: String): Boolean {
        return try {
            File(filePath).delete()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 批量删除文件
     */
    fun deleteFiles(filePaths: List<String>): Int {
        var count = 0
        filePaths.forEach { path ->
            if (deleteFile(path)) {
                count++
            }
        }
        return count
    }

    /**
     * 复制流
     */
    private fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(8192)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
    }

    /**
     * 获取目录总大小
     */
    fun getDirectorySize(directory: File): Long {
        if (!directory.exists()) return 0
        
        var size = 0L
        directory.listFiles()?.forEach { file ->
            size += if (file.isDirectory) {
                getDirectorySize(file)
            } else {
                file.length()
            }
        }
        
        return size
    }

    /**
     * 清空目录
     */
    fun clearDirectory(directory: File): Boolean {
        if (!directory.exists()) return true
        
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                clearDirectory(file)
            }
            file.delete()
        }
        
        return true
    }
}

package com.yourname.privatevault.util

/**
 * 文件名排序工具类
 * 从文件名中提取开头的数字进行排序
 * 示例：
 * "1.png" -> 1
 * "02.jpg" -> 2
 * "003.png" -> 3
 * "第 1 话.jpg" -> 1
 */
object FileNameSorter {

    /**
     * 从文件名中提取数字
     */
    fun extractNumber(fileName: String): Int {
        // 移除扩展名
        val nameWithoutExtension = fileName.substringBeforeLast(".")
        
        // 尝试提取连续的数字
        val numberString = nameWithoutExtension.takeWhile { it.isDigit() }
        
        // 如果没有找到数字，尝试提取中文数字后的阿拉伯数字
        if (numberString.isEmpty()) {
            val match = Regex("(\\d+)").find(nameWithoutExtension)
            return match?.groupValues?.get(1)?.toIntOrNull() ?: 0
        }
        
        return numberString.toIntOrNull() ?: 0
    }

    /**
     * 对文件名列表进行排序
     */
    fun sortFileNames(fileNames: List<String>): List<String> {
        return fileNames.sortedBy { extractNumber(it) }
    }

    /**
     * 对文件路径列表进行排序
     */
    fun sortFilePaths(filePaths: List<String>): List<String> {
        return filePaths.sortedBy { extractNumber(it.substringAfterLast('/')) }
    }
}
